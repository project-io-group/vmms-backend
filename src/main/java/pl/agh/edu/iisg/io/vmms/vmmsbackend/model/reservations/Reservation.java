package pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.User;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.VMPool;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
    @JsonManagedReference
    private VMPool pool;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.REMOVE)
    @OnDelete(action = OnDeleteAction.CASCADE)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(courseName, that.courseName) &&
                Objects.equals(pool, that.pool) &&
                Objects.equals(machinesNumber, that.machinesNumber) &&
                Objects.equals(createDate, that.createDate) &&
                Objects.equals(deadlineToConfirm, that.deadlineToConfirm) &&
                Objects.equals(confirmationDate, that.confirmationDate);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, courseName, pool, machinesNumber, createDate, deadlineToConfirm, confirmationDate);
    }
}
