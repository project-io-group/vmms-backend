package pl.agh.edu.iisg.io.vmms.vmmsbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Repository;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.VMPool;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations.Reservation;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> getByConfirmDateNotNullOrDeadlineToConfirmAfter(Date now);

    @Query(value = "select r from Reservation as r " +
            "where ( r.confirmDate is not null or r.deadlineToConfirm > :nowDate ) " +
            "and :fromDate < any ( select r.dates from r ) " +
            "and :toDate > any ( select r.dates from r ) ")
    List<Reservation> getAllValidByDatesBetween(
            @Param("nowDate") Date now,
            @Param("fromDate")Date from,
            @Param("toDate")Date to);

    @Query(value = "select r from Reservation as r " +
            "where r.pool.shortName = :vmPoolShortName " +
            "and (r.confirmDate is not null or r.deadlineToConfirm > :nowDate) " +
            "and :fromDate < any ( select r.dates from r ) " +
            "and :toDate > any ( select r.dates from r ) ")
    List<Reservation> getAllValidByDatesBetweenForVMPool(
            @Param("nowDate") Date now,
            @Param("fromDate") Date from,
            @Param("toDate")Date to,
            @Param("vmPoolShortName") String vmPoolShortName);

    @Query(value = "select r from Reservation as r " +
            "where r.pool = :vmPool "+
            "and (r.confirmDate is not null or r.deadlineToConfirm > :nowDate) " +
            "and :date = any ( select r.dates from r ) ")
    List<Reservation> findAllValidByPoolAndDate(
            @Param("nowDate") Date nowDate,
            @Param("vmPool") VMPool pool,
            @Param("date") Date date);

    @Query(nativeQuery = true, value = "insert into reservation_details (reservation_id, date) " +
            "values (:id, :date)")
    Reservation saveDateInReservation(
            @Param("id") Long reservationId,
            @Param("date") Date date);

    Optional<Reservation> getAllByIdAndConfirmDateNotNullOrDeadlineToConfirmBefore(Long id, Date now);

    Reservation getTopByDatesContainingAndPoolAndConfirmDateNotNullOrDeadlineToConfirmBeforeOrderByMachinesNumberDesc(List<Date> dates, VMPool pool, Date now);
}
