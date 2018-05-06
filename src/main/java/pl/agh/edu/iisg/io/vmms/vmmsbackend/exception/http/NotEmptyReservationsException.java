package pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.http;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.OK)
public class NotEmptyReservationsException extends HttpException {
    private final int httpStatus;

    public NotEmptyReservationsException() {
        super("Please remove all reservations first!");
        this.httpStatus = 400;
    }

    @Override
    public int getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getErrorDescription() {
        return "Not empty reservations!";
    }
}
