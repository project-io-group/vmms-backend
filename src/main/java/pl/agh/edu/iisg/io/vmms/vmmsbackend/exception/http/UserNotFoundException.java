package pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.http;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND)
public class UserNotFoundException extends HttpException {
    private final int httpStatus;

    public UserNotFoundException() {
        super("Please provide correct user data!");
        this.httpStatus = 404;
    }

    @Override
    public int getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getErrorDescription() {
        return "User not found!";
    }
}
