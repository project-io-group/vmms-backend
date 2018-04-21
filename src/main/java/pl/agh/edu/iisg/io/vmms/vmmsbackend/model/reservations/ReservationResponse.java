package pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class ReservationResponse {
    Reservation reservationMade;
    List<Reservation> collisionsWithDesired;
}
