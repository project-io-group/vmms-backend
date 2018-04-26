package pl.agh.edu.iisg.io.vmms.vmmsbackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;

@Data
@NoArgsConstructor
public class ReservationRequestDto {

    Long userId;
    Long vmPoolId;
    Integer machinesNumber;
    String courseName;

    @JsonFormat(pattern="HH:mm")
    Date startTime;

    @JsonFormat(pattern="HH:mm")
    Date endTime;

    @JsonFormat(pattern="yyyy-MM-dd")
    ArrayList<Date> dates;

}
