package pl.agh.edu.iisg.io.vmms.vmmsbackend.dto.reservation;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.dto.UserDto;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.dto.VMPoolDto;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class ReservationDto {

    Long id;
    UserDto owner;
    String courseName;
    VMPoolDto vmPool;
    Integer machinesNumber;

    @DateTimeFormat(pattern = "HH:mm")
    Date startTime;

    @DateTimeFormat(pattern = "HH:mm")
    Date endTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    List<Date> dates;

}
