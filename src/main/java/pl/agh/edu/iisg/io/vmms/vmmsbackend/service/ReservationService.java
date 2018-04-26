package pl.agh.edu.iisg.io.vmms.vmmsbackend.service;

import pl.agh.edu.iisg.io.vmms.vmmsbackend.dto.ReservationRequestDto;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.User;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.VMPool;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations.Reservation;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations.ReservationPeriod;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ReservationService {

    List<Reservation> getConfirmedOrBeforeDeadlineToConfirmReservations();

    List<Reservation> getConfirmedOrBeforeDeadlineToConfirmReservationsBetweenDates(Date from, Date to);

    Optional<Reservation> find(Long id);

    List<Reservation> getConfirmedOrBeforeDeadlineToConfirmReservationsBetweenDatesForVMPool(String vmPoolShortName, Date from, Date to);

    Long saveTemporary(Reservation reservation,
                       Date startTime,
                       Date endTime,
                       List<Date> days);

    Optional<Reservation> findIfNotExpired(Long id);

    void confirm(Reservation reservation);

    void delete(Reservation reservation);

    void deletePeriod(ReservationPeriod period);
}
