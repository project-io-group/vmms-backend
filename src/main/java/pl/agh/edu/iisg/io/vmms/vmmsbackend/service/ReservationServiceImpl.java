package pl.agh.edu.iisg.io.vmms.vmmsbackend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.constant.Mail;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.VMPool;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations.Reservation;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations.ReservationPeriod;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.repository.ReservationPeriodRepository;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.repository.ReservationRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Validated
public class ReservationServiceImpl implements ReservationService {

    private static final Logger logger = LoggerFactory.getLogger(ReservationServiceImpl.class);

    private final ReservationRepository reservationRepository;
    private final ReservationPeriodRepository reservationPeriodRepository;
    private final MailService mailService;
    private final MailSubjectService mailSubjectService;

    @Autowired
    ReservationServiceImpl(
            ReservationRepository reservationRepository,
            ReservationPeriodRepository reservationPeriodRepository,
            MailService mailService,
            MailSubjectService mailSubjectService) {
        this.reservationRepository = reservationRepository;
        this.reservationPeriodRepository = reservationPeriodRepository;
        this.mailService = mailService;
        this.mailSubjectService = mailSubjectService;
    }

    @Override
    public List<Reservation> getConfirmedOrBeforeDeadlineToConfirmReservations() {
        return reservationRepository
                .getByConfirmationDateNotNullOrDeadlineToConfirmAfter(new Date());
    }

    @Override
    public List<Reservation> getConfirmedOrBeforeDeadlineToConfirmReservationsBetweenDates(Date from, Date to) {
        return reservationRepository
                .getAllValidByDatesBetween(new Date(), from, to);
    }

    @Override
    public Optional<Reservation> find(Long id) {
        return reservationRepository.findById(id);
    }

    @Override
    public List<Reservation> getConfirmedOrBeforeDeadlineToConfirmReservationsBetweenDatesForVMPool(String vmPoolShortName, Date from, Date to) {
        return reservationRepository
                .getAllValidByDatesBetweenForVMPool(new Date(), from, to, vmPoolShortName);
    }

    @Transactional
    @Override
    public Reservation saveTemporary(Reservation reservation,
                                     Date startTime,
                                     Date endTime,
                                     List<Date> days) {

        Date now = new Date();
        reservation.setCreateDate(now);
        reservation.setDeadlineToConfirmAccordingToCreationTime(now);
        Reservation r = reservationRepository.save(reservation);
        r.setPeriods(new ArrayList<>());
        for (Date day : days) {

            Date from = new Date(day.getTime() + startTime.getTime());
            Date to = new Date(day.getTime() + endTime.getTime());
            try {
                ReservationPeriod reservationPeriod = new ReservationPeriod(from, to, r);
                //condition to be removed if autowiring in Validator is fixed
                if (isReservationPeriodValid(reservationPeriod)) {
                    r.addPeriod(reservationPeriodRepository.save(reservationPeriod));
                }
                //
            } catch (Exception e) {
                System.out.println("Collision");
                e.printStackTrace();
            }
        }
        return r;
    }

    @Override
    public Optional<Reservation> findIfNotExpired(Long id) {
        return reservationRepository.getIfConfirmedOrBeforeDeadline(id, new Date());
    }

    @Override
    public void confirm(Reservation reservation) {

        reservation.setConfirmationDate(new Date());
        reservationRepository.save(reservation);

        if (!reservation.getPool().getEnabled()) {
            notifyAdmin(reservation);
        }

    }

    private void notifyAdmin(Reservation reservation) {
        try {
            String subject = mailSubjectService
                    .find(Mail.SubjectKeys.ENABLE_POOL)
                    .orElse(Mail.Subjects.ENABLE_POOL);

            VMPool pool = reservation.getPool();

            String content = "Pool: " + pool.getShortName() + " / " + pool.getDisplayName() +
                    "\n\nUser: " + reservation.getOwner().getEmail() +
                    "\n\nCourse: " + reservation.getCourseName();

            mailService.sendToAllAdmins(subject, content);

        } catch (Exception e) {
            logger.warn("Admin won't be notified about reservation on disabled vm pool", e);
        }
    }

    @Override
    public void delete(Reservation reservation) {
        reservationRepository.delete(reservation);
    }

    @Override
    public void deletePeriod(ReservationPeriod period) {
        reservationPeriodRepository.delete(period);
    }

    @Override
    public void deleteExpiredOrEmptyReservations() {
        reservationRepository.deleteExpiredOrEmptyReservations(new Date());
    }

    private boolean isReservationPeriodValid(
            ReservationPeriod reservationPeriod) {

        if (reservationPeriod.getStartDate() == null
                || reservationPeriod.getEndDate() == null
                || reservationPeriod.getReservation() == null) {
            return false;
        }
        Date now = new Date();
        if (reservationPeriod.getStartDate().before(now)
                || reservationPeriod.getEndDate().before(now)) {
            return false;
        }

      /*  try {
            new ObjectMapper().writeValue(System.out, reservationPeriod);
        }catch (Exception e){
            e.printStackTrace();
        }*/

        Reservation reservation = reservationPeriod.getReservation();
        List<ReservationPeriod> periodsColliding = reservationPeriodRepository
                .getAllPeriodsForVMPoolBetween(reservation.getPool().getId(),
                        now,
                        reservationPeriod.getStartDate(),
                        reservationPeriod.getEndDate());

        Integer maxNumberReserved = getMaxMachinesCountInOneTime(periodsColliding);
        return maxNumberReserved + reservation.getMachinesNumber()
                <= reservation.getPool().getMaximumCount();
    }


    private Integer getMaxMachinesCountInOneTime(List<ReservationPeriod> periods) {

        if (periods.isEmpty()) {
            return 0;
        }

        List<ReservationPeriod> sortedByStartDate = periods
                .stream()
                .sorted((x, y) -> {
                    if (x.getStartDate().before(y.getStartDate()))
                        return -1;
                    return 1;
                })
                .collect(Collectors.toList());

        List<ReservationPeriod> sortedByEndDate = periods
                .stream()
                .sorted((x, y) -> {
                    if (x.getEndDate().before(y.getEndDate())) {
                        return -1;
                    }
                    return 1;
                })
                .collect(Collectors.toList());

        Date currentDate;
        int currentMachinesNumber = 0;
        int max = 0;
        int j = 0;
        for (int i = 0; i < periods.size(); i++) {
            currentDate = sortedByStartDate.get(i).getStartDate();
            currentMachinesNumber += sortedByStartDate.get(i).getReservation().getMachinesNumber();

            while (!sortedByEndDate.get(j).getEndDate().after(currentDate)) {
                currentMachinesNumber -= sortedByEndDate.get(j).getReservation().getMachinesNumber();
                j++;
            }
            if (max < currentMachinesNumber) {
                max = currentMachinesNumber;
            }
        }
        return max;
    }
}
