package pl.agh.edu.iisg.io.vmms.vmmsbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations.Reservation;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations.ReservationPeriod;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.repository.ReservationPeriodRepository;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.repository.ReservationRepository;

import javax.validation.Valid;
import java.util.*;

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
        r.setPeriods(new ArrayList<ReservationPeriod>());
        for(Date day : days){
            Date from = new Date(day.getTime() + startTime.getTime());
            Date to = new Date(day.getTime() + endTime.getTime());
            try {
                @Valid ReservationPeriod reservationPeriod = new ReservationPeriod(from, to, r);
                r.addPeriod(reservationPeriodRepository.save( reservationPeriod));
            }catch(Exception e){
                System.out.println("Collision");
                e.printStackTrace();
            }
        }
        return reservationRepository.findById(reservation.getId()).get();
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
}
