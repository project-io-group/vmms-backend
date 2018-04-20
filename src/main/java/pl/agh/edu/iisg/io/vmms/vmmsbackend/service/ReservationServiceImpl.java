package pl.agh.edu.iisg.io.vmms.vmmsbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.User;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.VMPool;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations.Reservation;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations.ReservationResponse;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations.SingleReservation;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.repository.ReservationRepository;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.repository.UserRepository;

import java.util.Date;
import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {

    ReservationRepository reservationRepository;

    @Autowired
    ReservationServiceImpl(ReservationRepository reservationRepository){
    }

    @Override
    public List<Reservation> getReservationsBetweenDates(Date from, Date to) {
        return reservationRepository.findAllWithDatesBetween(from, to);
    }

    @Override
    public List<Reservation> getReservations() {
        return reservationRepository.getByConfirmDateNotNull();
    }

    @Override
    public ReservationResponse saveTemporarySingle(User user, VMPool vmPool, String courseName, Integer machinesCount, Date date) {
        Reservation reservation = new SingleReservation();
        reservation.setOwner(user);
        reservation.setPool(vmPool);
        reservation.setCourseName(courseName);
        reservation.setMachinesNumber(machinesCount);
        reservation.setCreateDate(new Date());
        ReservationResponse response = new ReservationResponse();

        try {
            reservationRepository.save(reservation);
        }catch(Exception e ){
            response.setCollisions(null); //TODO
        }
        response.setReservation(reservation);
        return response;
    }

    @Override
    public ReservationResponse saveTemporaryCyclic(User user, VMPool vmPool, String courseName, Integer machinesCount, Date from, Date to, Integer intercal) {
        return null;
    }

    @Override
    public Reservation find(Long id) {
        return null;
    }

    @Override
    public Reservation firstByDate(Date date) {
        return null;
    }

    @Override
    public String confirm(Long reservationId) {
        return null;
    }
}
