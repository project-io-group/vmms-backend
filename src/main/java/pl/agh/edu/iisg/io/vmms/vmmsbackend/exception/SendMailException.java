package pl.agh.edu.iisg.io.vmms.vmmsbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
public class SendMailException extends Exception {

    public SendMailException(Throwable exception) {
        super(exception);
    }

    public SendMailException(String message) {
        super(message);
    }
}
