package pl.agh.edu.iisg.io.vmms.vmmsbackend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
public class ReservationRequestDto {

    Long userId;
    Long vmPoolId;
    Integer machinesNumber;
    String courseName;
    ArrayList<ReservationPeriodDto> dates;

}
