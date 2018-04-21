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

    List<Reservation> getByConfirmDateNotNullOrDeadlineToConfirmBefore(Date now);

    @Query(nativeQuery = true, value = "select r, d.date from reservation as r " +
            "inner join reservations_details as d on r.id = d.reservation_id " +
            "where (r.confirm_date is not null or r.deadline_to_confirm > :nowDate) " +
            "and d.date between :fromDate and :toDate " +
            "group by r.id ")
    List<Reservation> getAllValidByDatesBetween(
            @Param("nowDate") Date now,
            @Param("fromDate")Date from,
            @Param("toDate")Date to);

    @Query(nativeQuery = true, value = "select r, d.date from reservation as r" +
            "inner join reservations_details as d on r.id = d.reservation_id " +
            "inner join vmpool as p on r.pool_id = p.id " +
            "where p.short_name = :vmPoolShortName " +
            "and (r.confirm_date is not null or r.deadline_to_confirm > :nowDate) " +
            "and d.date between :fromDate and :toDate " +
            "group by r.id ")
    List<Reservation> getAllValidByDatesBetweenForVMPool(
            @Param("nowDate") Date now,
            @Param("fromDate") Date from,
            @Param("toDate")Date to,
            @Param("vmPoolShortName") String vmPoolShortName);

    @Query(nativeQuery = true, value = "select d.date from reservation " +
            "inner join reservations_details d on r.id = d.reservation_id " +
            "where r.pool_id = :vmPool.getId() "+
            "and (r.confirm_date is not null or r.deadline_to_confirm > :nowDate) " +
            "and d.date in :dates " +
            "group by d.date " +
            "having sum(r.machines_number) > :vmPool.getMaximumCount() ")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm") List<Date>
    findAllValidByPoolAndCollidingWithDates(
            @Param("nowDate") Date nowDate,
            @Param("vmPool") VMPool pool,
            @Param("dates") List<Date> dates);

    Optional<Reservation> getAllByIdAndConfirmDateNotNullOrDeadlineToConfirmBefore(Long id, Date now);

    Reservation getTopByDatesContainingAndPoolAndConfirmDateNotNullOrDeadlineToConfirmBeforeOrderByMachinesNumberDesc(List<Date> dates, VMPool pool, Date now);
}
