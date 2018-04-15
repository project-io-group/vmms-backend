package pl.agh.edu.iisg.io.vmms.vmmsbackend.model;

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
@Table(name = "vm_pools")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class VMPool {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique=true, nullable=false)
    private String shortName;

    @NotNull
    private String displayName;

    @Min(0)
    @NotNull
    private Integer maximumCount;

    @NotNull
    private Boolean enabled;

    private String description;

    @OneToMany(mappedBy = "pool")
    public Set<Reservation> reservations;

    public abstract static class Builder{
        private String shortName;
        private String displayName;
        private Integer maximumCount;
        private Boolean enabled;
        private String description;

        public Builder shortName(String shortName) {
            this.shortName = shortName;
            return this;
        }

        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder maximumCount(Integer maximumCount) {
            this.maximumCount = maximumCount;
            return this;
        }

        public Builder enabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public abstract VMPool build();
    }

    public static Builder builder() {
        return new Builder()
        {
            @Override
            public VMPool build()
            {
                return new VMPool(this);
            }
        };
    }

    private VMPool(Builder builder){
        shortName = builder.shortName;
        displayName = builder.displayName;
        maximumCount = builder.maximumCount;
        enabled = builder.enabled;
        description = builder.description;
    }
}
