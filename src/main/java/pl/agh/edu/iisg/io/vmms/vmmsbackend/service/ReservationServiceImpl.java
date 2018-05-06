package pl.agh.edu.iisg.io.vmms.vmmsbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
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

    private final ReservationRepository reservationRepository;
    private final ReservationPeriodRepository reservationPeriodRepository;

    @Autowired
    ReservationServiceImpl(ReservationRepository reservationRepository,
                           ReservationPeriodRepository reservationPeriodRepository) {
        this.reservationRepository = reservationRepository;
        this.reservationPeriodRepository = reservationPeriodRepository;
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
        for(Date day : days){

            Date from = new Date(day.getTime() + startTime.getTime());
            Date to = new Date(day.getTime() + endTime.getTime());
            try {
                ReservationPeriod reservationPeriod = new ReservationPeriod(from, to, r);
                //condition to be removed if autowiring in Validator is fixed
                if(isValid(reservationPeriod)){
                    r.addPeriod(reservationPeriodRepository.save( reservationPeriod));
                }
                //
            }catch(Exception e){
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
    }

    @Override
    public void delete(Reservation reservation) {
        reservationRepository.delete(reservation);
    }

    @Override
    public void deletePeriod(ReservationPeriod period){
        reservationPeriodRepository.delete(period);
    }

    //copied from Validator (to be removed when autowiring in Validator is fixed)
    private boolean isValid(
            ReservationPeriod reservationPeriod) {

        if (reservationPeriod.getStartDate() == null
                || reservationPeriod.getEndDate() == null
                || reservationPeriod.getReservation() == null) {
            return false;
        }
        Date now = new Date();
        if(reservationPeriod.getStartDate().before(now)
                || reservationPeriod.getEndDate().before(now))
            return false;

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


    private Integer getMaxMachinesCountInOneTime(List<ReservationPeriod> periods){

        if (periods.isEmpty()){
            return 0;
        }

        List<ReservationPeriod> sortedByStartDate = periods
                .stream()
                .sorted((x, y) -> {
                    if(x.getStartDate().before(y.getStartDate()))
                        return -1;
                    return 1;
                } )
                .collect(Collectors.toList());

        List<ReservationPeriod> sortedByEndDate = periods
                .stream()
                .sorted((x, y) -> {
                    if(x.getEndDate().before(y.getEndDate()))
                        return -1;
                    return 1;
                } )
                .collect(Collectors.toList());

        Date currentDate;
        int currentMachinesNumber = 0;
        int max = 0;
        int j = 0;
        for(int i=0; i<periods.size(); i++){
            currentDate = sortedByStartDate.get(i).getStartDate();
            currentMachinesNumber += sortedByStartDate.get(i).getReservation().getMachinesNumber();

            while(!sortedByEndDate.get(j).getEndDate().after(currentDate)){
                currentMachinesNumber -= sortedByEndDate.get(j).getReservation().getMachinesNumber();
                j++;
            }
            if(max < currentMachinesNumber){
                max = currentMachinesNumber;
            }
        }
        return max;
    }
}
