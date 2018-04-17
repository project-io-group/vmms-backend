package pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.http;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST)
public class MissingUserIdException extends HttpException {
    private final int httpStatus;

    public MissingUserIdException() {
        super("Please provide user id!");
        this.httpStatus = 400;
    }

    @Override
    public int getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getErrorDescription() {
        return "Missing user id!";
    }
}
