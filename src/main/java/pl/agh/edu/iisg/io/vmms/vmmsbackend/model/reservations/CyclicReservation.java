package pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Entity
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@DiscriminatorValue(value = "Cyclic")
public class CyclicReservation extends Reservation {

    public void setDates(Date firstReservationDate, Date lastReservationDate, int intervalInDays){

        LocalDate first = firstReservationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate last = lastReservationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        Stream<Date> dateStream = Stream.iterate(first, date -> date.plusDays(intervalInDays))
                .limit(ChronoUnit.DAYS.between(first, last) + 2)
                .map(date ->
                        Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));

        List<Date> date = dateStream.collect(Collectors.toList());
        super.setDates(date);
    }
}
