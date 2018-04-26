package pl.agh.edu.iisg.io.vmms.vmmsbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.VMPool;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations.ReservationPeriod;

import java.time.Period;
import java.util.Date;
import java.util.List;


@Repository
public interface ReservationPeriodRepository extends JpaRepository<ReservationPeriod, Long> {

    @Query(value = "select r from ReservationPeriod as r " +
            "where r.reservation.pool = :pool " +
            "and (  r.reservation.confirmationDate is not null " +
            "       or r.reservation.deadlineToConfirm > :nowDate ) " +
            "and :fromDate < any (select p.endDate from r.reservation.periods as p )" +
            "and :toDate > any (select p.startDate from r.reservation.periods as p )")
    List<ReservationPeriod> getAllPeriodsForVMPoolBetween(
            @Param("pool") VMPool pool,
            @Param("nowDate") Date now,
            @Param("fromDate") Date from,
            @Param("toDate") Date to);
}
