package pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.http;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST)
public class MissingUserNameException extends HttpException {
    private final int httpStatus;

    public MissingUserNameException() {
        super("Please provide user name!");
        this.httpStatus = 400;
    }

    @Override
    public int getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getErrorDescription() {
        return "Missing user name!";
    }
}
