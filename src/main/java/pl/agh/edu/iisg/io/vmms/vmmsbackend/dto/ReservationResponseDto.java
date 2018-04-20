package pl.agh.edu.iisg.io.vmms.vmmsbackend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations.Reservation;

import java.util.List;

@Data
@NoArgsConstructor
public class ReservationResponseDto {

    ReservationDto reservationDto;
    List<ReservationDto> collisions;
}
