package pl.agh.edu.iisg.io.vmms.vmmsbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST)
public class InvalidEmailRequestException extends Exception {
    private final String errorDescription;

    public InvalidEmailRequestException(String errorDescription) {
        super(errorDescription);
        this.errorDescription = errorDescription;
    }
}
