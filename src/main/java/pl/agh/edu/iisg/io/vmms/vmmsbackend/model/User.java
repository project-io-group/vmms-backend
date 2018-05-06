package pl.agh.edu.iisg.io.vmms.vmmsbackend.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations.Reservation;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@Table(name = "users")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {

    @OneToMany(mappedBy = "owner", cascade = CascadeType.REMOVE)
    @JsonManagedReference
    public Set<Reservation> reservations;
    @Id
    @GeneratedValue
    private Long id;
    @NotNull
    private String userName;

    @NotNull
    private boolean isAdmin;

}
