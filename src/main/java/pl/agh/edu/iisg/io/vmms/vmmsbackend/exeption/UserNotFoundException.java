package pl.agh.edu.iisg.io.vmms.vmmsbackend.exeption;


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
