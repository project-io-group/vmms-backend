package pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@DiscriminatorValue(value = "Cyclic")
public class CyclicReservation extends Reservation {

    public void setDates(Date startDateInclusive, Date endDateExclusive, Period cycleInterval) {

        LocalDate start = convertToLocal(startDateInclusive);
        LocalDate end = convertToLocal(endDateExclusive);

        Stream<Date> cyclicDates = Stream.iterate(start, date -> date.plus(cycleInterval))
                .limit(
                        ChronoUnit.DAYS.between(start, end) / cycleInterval.getDays()
                ).map(date ->
                        Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())
                );

        List<Date> dates = cyclicDates.collect(Collectors.toList());
        super.setDates(dates);
    }

    private LocalDate convertToLocal(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

}
