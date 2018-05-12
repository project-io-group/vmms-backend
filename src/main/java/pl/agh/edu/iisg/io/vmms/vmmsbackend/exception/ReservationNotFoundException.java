package pl.agh.edu.iisg.io.vmms.vmmsbackend.exception;

import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.http.HttpException;

public class ReservationNotFoundException extends HttpException {
    private final int httpStatus;
    private final String errorDescription;

    public ReservationNotFoundException() {
        super("There is no reservation with this ID.");
        this.httpStatus = 404;
        this.errorDescription = "There is no reservation with this ID.";
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
