package pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@DiscriminatorValue(value = "Single")
public class SingleReservation extends Reservation {

    public static abstract class Builder<T extends Reservation> extends Reservation.Builder<T> {
        private Date reservedDate;

        public Builder<T> reservedDate(Date date) {
            this.reservedDate = date;
            return this;
        }
    }

    public static Builder<?> builder() {
        return new Builder<SingleReservation>()
        {
            @Override
            public SingleReservation build()
            {
                return new SingleReservation(this);
            }
        };
    }

    private SingleReservation(Builder builder){
        super(builder);
        super.addDate(builder.reservedDate);
    }
}
