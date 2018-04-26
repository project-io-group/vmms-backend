package pl.agh.edu.iisg.io.vmms.vmmsbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.VMPool;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations.Reservation;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> getByConfirmationDateNotNullOrDeadlineToConfirmAfter(Date now);

    @Query(value = "select r from Reservation as r " +
            "where ( r.confirmationDate is not null or r.deadlineToConfirm > :nowDate ) " +
            "and :fromDate < any (select p.endDate from r.periods as p )" +
            "and :toDate > any (select p.startDate from r.periods as p )")
    List<Reservation> getAllValidByDatesBetween(
            @Param("nowDate") Date now,
            @Param("fromDate") Date from,
            @Param("toDate") Date to);

    @Query(value = "select r from Reservation as r " +
            "where r.pool.shortName = :vmPoolShortName " +
            "and (r.confirmationDate is not null or r.deadlineToConfirm > :nowDate) " +
            "and :fromDate < any ( select p.endDate from r.periods as p) " +
            "and :toDate > any ( select p.startDate from r.periods as p) ")
    List<Reservation> getAllValidByDatesBetweenForVMPool(
            @Param("nowDate") Date now,
            @Param("fromDate") Date from,
            @Param("toDate") Date to,
            @Param("vmPoolShortName") String vmPoolShortName);

    @Query(value = "select r from Reservation as r " +
            "where r.id = :id " +
            "and (r.confirmationDate is not null or r.deadlineToConfirm > :now)")
    Optional<Reservation> getIfConfirmedOrBeforeDeadline(@Param("id") Long id, @Param("now") Date now);

}
