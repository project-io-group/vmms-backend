package pl.agh.edu.iisg.io.vmms.vmmsbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.dto.ReservationRequestDto;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.User;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.VMPool;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations.Reservation;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations.ReservationPeriod;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.repository.ReservationPeriodRepository;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.repository.ReservationRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;

    @Autowired
    ReservationServiceImpl(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
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
    public Long saveTemporary(Reservation reservation,
                              Date startTime,
                              Date endTime,
                              List<Date> days) {

        Date now = new Date();
        reservation.setCreateDate(now);
        reservation.setDeadlineToConfirmAccordingToCreationTime(now);
        Reservation r = reservationRepository.save(reservation);

        for(Date day : days){
            Date from = new Date(day.getTime() + startTime.getTime());
            Date to = new Date(day.getTime() + endTime.getTime());
            try {
                ReservationPeriod reservationPeriod = new ReservationPeriod(from, to, r);
                //save reservationPeriod before?
                r.addPeriod(reservationPeriod);
            }catch(Exception e){
                System.out.println("Collision");
            }
        }
        return r.getId();
    }

    @Override
    public Optional<Reservation> findIfNotExpired(Long id) {
        return reservationRepository.getIfConfirmedOrBeforeDeadline(id, new Date());
    }

    @Override
    public Reservation confirm(Reservation reservation) {

        reservation.setConfirmationDate(new Date());
        return reservationRepository.save(reservation);
    }

    @Override
    public void delete(Reservation reservation) {
        reservationRepository.delete(reservation);
    }
}
