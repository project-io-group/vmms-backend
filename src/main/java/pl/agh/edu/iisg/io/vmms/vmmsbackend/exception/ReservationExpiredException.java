package pl.agh.edu.iisg.io.vmms.vmmsbackend.exception;

import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.http.HttpException;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.reservations.Reservation;

public class ReservationExpiredException extends HttpException {
    private final int httpStatus;
    private final String errorDescription;

    public ReservationExpiredException() {
        super("Confirmation time exceeded!");
        this.httpStatus = 400;
        this.errorDescription = "Confirmation time exceeded!";
    }

    @Override
    public int getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getErrorDescription() {
        return errorDescription;
    }
}
