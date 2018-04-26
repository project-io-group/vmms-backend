package pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.validator.ValidReservationPeriod;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity
public class ReservationPeriod {

    public ReservationPeriod(Date startDate, Date endDate, Reservation reservation){
        this.startDate = startDate;
        this.endDate = endDate;
        this.reservation = reservation;
    }

    @Id
    @GeneratedValue
    private Long id;

    @Column(columnDefinition = "TIMESTAMP")
    private Date startDate;

    @Column(columnDefinition = "TIMESTAMP")
    private Date endDate;

    @ManyToOne
    @JoinColumn
    private Reservation reservation;

}
