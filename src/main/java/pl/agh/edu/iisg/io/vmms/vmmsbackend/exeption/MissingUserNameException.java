package pl.agh.edu.iisg.io.vmms.vmmsbackend.exeption;


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
