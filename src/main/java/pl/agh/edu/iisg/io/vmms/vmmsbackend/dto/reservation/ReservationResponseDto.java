package pl.agh.edu.iisg.io.vmms.vmmsbackend.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponseDto {

    Long id;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    List<Date> daysNotReserved;
}
