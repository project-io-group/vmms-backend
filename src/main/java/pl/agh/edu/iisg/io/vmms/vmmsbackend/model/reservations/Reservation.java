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
    @NotNull
    @ElementCollection
    @CollectionTable(
            name = "reservations_details",
            joinColumns = @JoinColumn(name = "reservationId")
    )
    private List<Date> dates;

    @NotNull
    @Min(0)
    private Integer machinesNumber;

    @NotNull
    private Boolean temporary;

    @Column(columnDefinition = "TIMESTAMP")
    private Date createDate;

    @Column(columnDefinition = "TIMESTAMP")
    private Date confirmDate;
}
