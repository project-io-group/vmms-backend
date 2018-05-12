package pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.http;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST)
public class MissingReservationFieldException extends HttpException {
    private final int httpStatus;

    public MissingReservationFieldException() {
        super("Please fulfill all required fields!");
        this.httpStatus = 400;
    }

    @Override
    public int getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getErrorDescription() {
        return "Something is missing!";
    }
}
