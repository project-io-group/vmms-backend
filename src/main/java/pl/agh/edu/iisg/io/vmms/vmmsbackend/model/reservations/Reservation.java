package pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.User;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.VMPool;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "reservation_type")
public abstract class Reservation {

    @Transient
    public static final Integer EXPIRATION_TIME = 20*60;

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User owner;

    private String courseName;

    @ManyToOne
    @JoinColumn(name = "poolId")
    private VMPool pool;

    @Column(name = "date", columnDefinition = "TIMESTAMP")
    @ElementCollection
    @CollectionTable(
            name = "reservations_details",
            joinColumns = @JoinColumn(name = "reservationId")
    )
    private List<Date> dates;

    @NotNull
    @Min(0)
    private Integer machinesNumber;

    @Column(columnDefinition = "TIMESTAMP")
    private Date createDate;

    @Column(columnDefinition = "TIMESTAMP")
    private Date deadlineToConfirm;

    @Column(columnDefinition = "TIMESTAMP")
    private Date confirmationDate;

    public void setDeadlineToConfirmAccordingToCreationTime(Date date){
        Long creationTime = date.getTime();
        Long expirationTime = creationTime + EXPIRATION_TIME;
        deadlineToConfirm = new Date(expirationTime);
    }
}
