package pl.agh.edu.iisg.io.vmms.vmmsbackend.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.dto.UserDto;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.dto.VMPoolDto;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Data
@NoArgsConstructor
public class ReservationDto {

    private Long id;

    private UserDto owner;

    private String courseName;

    private VMPoolDto vmPool;

    private Integer machinesNumber;

    @JsonFormat(pattern="HH:mm")
    Date startTime;

    @JsonFormat(pattern="HH:mm")
    Date endTime;

    @JsonFormat(pattern="yyyy-MM-dd")
    List<Date> dates;

}
