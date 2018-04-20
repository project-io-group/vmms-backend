package pl.agh.edu.iisg.io.vmms.vmmsbackend.service;

import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.User;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.VMPool;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations.Reservation;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations.ReservationResponse;

import java.util.Date;
import java.util.List;

public interface ReservationService {

    List<Reservation> getReservationsBetweenDates(Date from, Date to);

    List<Reservation> getReservations();

    ReservationResponse saveTemporarySingle(User user,
                                            VMPool vmPool,
                                            String courseName,
                                            Integer machinesCount,
                                            Date date);

    ReservationResponse saveTemporaryCyclic(User user,
                                            VMPool vmPool,
                                            String courseName,
                                            Integer machinesCount,
                                            Date from,
                                            Date to,
                                            Integer intercal);

    Reservation find(Long id);

    Reservation firstByDate(Date date);

    String confirm(Long reservationId);
}
