package pl.agh.edu.iisg.io.vmms.vmmsbackend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations.Reservation;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class VMPool {

    @OneToMany(mappedBy = "pool")
    @JsonBackReference
    public Set<Reservation> reservations;
    @Id
    @GeneratedValue
    private Long id;
    @Column(unique = true, nullable = false)
    private String shortName;
    @NotNull
    private String displayName;
    @Min(0)
    @NotNull
    private Integer maximumCount;
    @NotNull
    private Boolean enabled;
    @Column(length = 2048)
    private String description;
}
