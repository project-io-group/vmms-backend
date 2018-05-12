package pl.agh.edu.iisg.io.vmms.vmmsbackend.exception;

import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.http.HttpException;

public class ReservationDateNotFoundException extends HttpException {
    private final int httpStatus;
    private final String errorDescription;

    public ReservationDateNotFoundException() {
        super("Reservation with this ID is not scheduled for given date.");
        this.httpStatus = 404;
        this.errorDescription = "Reservation with this ID is not scheduled for given date.";
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
