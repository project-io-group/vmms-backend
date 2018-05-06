package pl.agh.edu.iisg.io.vmms.vmmsbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNullApi;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.VMPool;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations.ReservationPeriod;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.validator.ValidReservationPeriod;

import javax.validation.Valid;
import java.time.Period;
import java.util.Date;
import java.util.List;

@Repository
public interface ReservationPeriodRepository extends JpaRepository<ReservationPeriod, Long> {

    @Query(value = "select r from ReservationPeriod as r " +
            "where r.reservation.pool = :pool " +
            "and (  r.reservation.confirmationDate is not null " +
            "       or r.reservation.deadlineToConfirm > :nowDate ) " +
            "and :fromDate < r.endDate " +
            "and :toDate > r.startDate")
    List<ReservationPeriod> getAllPeriodsForVMPoolBetween(
            @Param("pool") VMPool pool,
            @Param("nowDate") Date now,
            @Param("fromDate") Date from,
            @Param("toDate") Date to);

}
