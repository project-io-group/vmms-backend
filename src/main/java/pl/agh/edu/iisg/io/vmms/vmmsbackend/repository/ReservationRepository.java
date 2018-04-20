package pl.agh.edu.iisg.io.vmms.vmmsbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations.Reservation;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("select r from reservation r inner join r.date " +
            "where d.date between :fromDate and :toDate " +
            "and r.confirmDate is not null")
    List<Reservation> findAllWithDatesBetween(@Param("fromDate")Date from, @Param("toDate")Date to);

    List<Reservation> getByConfirmDateNotNull();



}
