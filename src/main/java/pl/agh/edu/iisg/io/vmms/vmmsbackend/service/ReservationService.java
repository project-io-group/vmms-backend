package pl.agh.edu.iisg.io.vmms.vmmsbackend.service;

import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.User;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.VMPool;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations.Reservation;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations.ReservationResponse;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ReservationService {

    List<Reservation> getConfirmedOrBeforeDeadlineToConfirmReservations();

    List<Reservation> getConfirmedOrBeforeDeadlineToConfirmReservationsBetweenDates(Date from, Date to);

    Optional<Reservation> find(Long id);

    List<Reservation> getConfirmedOrBeforeDeadlineToConfirmReservationsBetweenDatesForVMPool(String vmPoolShortName, Date from, Date to);

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
                                            Integer interval);


    Optional<Reservation> findIfNotExpired(Long id);

    Reservation confirm(Reservation reservation);
}
