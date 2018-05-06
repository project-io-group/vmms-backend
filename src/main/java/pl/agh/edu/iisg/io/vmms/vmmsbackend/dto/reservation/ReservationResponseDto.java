package pl.agh.edu.iisg.io.vmms.vmmsbackend.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponseDto {

    private Long id;

    @JsonFormat(pattern="yyyy-MM-dd")
    private List<Date> daysNotReserved;
}
