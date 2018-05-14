package pl.agh.edu.iisg.io.vmms.vmmsbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Paweł Taborowski on 14.05.18.
 */
@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
public class MailSendingFailureException extends Exception {

    public MailSendingFailureException(Throwable exception) {
        super(exception);
    }
}
