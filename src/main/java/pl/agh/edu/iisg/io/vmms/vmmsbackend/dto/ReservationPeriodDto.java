package pl.agh.edu.iisg.io.vmms.vmmsbackend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@NoArgsConstructor
public class ReservationPeriodDto {

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    Date startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    Date endDate;
}
