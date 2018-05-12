package pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.NoArgsConstructor;

import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.User;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.VMPool;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.Date;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Reservation {

    @Transient
    public static final Duration EXPIRATION_TIME = Duration.ofMinutes(20);

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonBackReference
    private User owner;

    private String courseName;

    @ManyToOne
    @JoinColumn(name = "poolId")
    private VMPool pool;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<ReservationPeriod> periods;

    @NotNull
    @Min(0)
    private Integer machinesNumber;

    @Column(columnDefinition = "TIMESTAMP")
    private Date createDate;

    @Column(columnDefinition = "TIMESTAMP")
    private Date deadlineToConfirm;

    @Column(columnDefinition = "TIMESTAMP")
    private Date confirmationDate;

    public void setDeadlineToConfirmAccordingToCreationTime(Date date) {
        deadlineToConfirm = Date.from(date.toInstant().plus(EXPIRATION_TIME));
    }

    public void addPeriod(ReservationPeriod period){
        this.periods.add(period);
    }

    public void removePeriod(ReservationPeriod period){
        this.periods.remove(period);
    }

    public boolean isExpired(){
        return confirmationDate == null && deadlineToConfirm.before(new Date());
    }
}
