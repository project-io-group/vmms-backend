package pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.User;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.VMPool;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "reservations")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "reservation_type")
public abstract class Reservation {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name="userId")
    private User owner;

    private String courseName;

    @ManyToOne
    @JoinColumn(name="poolId")
    private VMPool pool;

    @Column(name = "date",columnDefinition="TIMESTAMP")
    @NotNull
    @ElementCollection
    @CollectionTable(
            name="reservations_details",
            joinColumns=@JoinColumn(name="reservationId")
    )
    private List<Date> dates;

    @NotNull
    @Min(0)
    private Integer machinesNumber;

    public abstract static class Builder<T extends Reservation>{
        private VMPool pool;
        private User owner;
        private String courseName;
        private Integer machinesNumber;

        public Builder<T> pool(VMPool pool) {
            this.pool = pool;
            return this;
        }

        public Builder<T> owner(User owner) {
            this.owner = owner;
            return this;
        }

        public Builder<T> courseName(String courseName) {
            this.courseName = courseName;
            return this;
        }

        public Builder<T> machinesNumber(Integer machinesNumber) {
            this.machinesNumber = machinesNumber;
            return this;
        }
        public abstract T build();
    }

    Reservation(Builder builder){
        this.pool = builder.pool;
        this.owner = builder.owner;
        this.courseName = builder.courseName;
        this.dates = new ArrayList<>();
        this.machinesNumber = builder.machinesNumber;
    }

    void addDate(Date date){
        dates.add(date);
    }
}
