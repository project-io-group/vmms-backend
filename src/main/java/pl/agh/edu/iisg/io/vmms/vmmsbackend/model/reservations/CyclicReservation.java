package pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.stream.Stream;

@Data
@Entity
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@DiscriminatorValue(value = "Cyclic")
public class CyclicReservation extends Reservation {

    public static abstract class Builder<T extends Reservation> extends Reservation.Builder<T> {
        private Date firstReservationDate;
        private Date lastReservationDate;
        private int intervalInDays;

        public Builder<T> firstReservationDate(Date date) {
            this.firstReservationDate = date;
            return this;
        }

        public Builder<T> lastReservationDate(Date date) {
            this.lastReservationDate = date;
            return this;
        }

        public Builder<T> intervalInDays(int days) {
            this.intervalInDays = days;
            return this;
        }
    }

    public static Builder<?> builder() {
        return new Builder<CyclicReservation>()
        {
            @Override
            public CyclicReservation build()
            {
                return new CyclicReservation(this);
            }
        };
    }

    private CyclicReservation(Builder builder){
        super(builder);

        LocalDate first = builder.firstReservationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate last = builder.lastReservationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        Stream.iterate(first, date -> date.plusDays(builder.intervalInDays))
                .limit(ChronoUnit.DAYS.between(first, last) + 2)
                .forEach(date -> super.addDate(
                        Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())));
    }
}
