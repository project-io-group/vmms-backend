package pl.agh.edu.iisg.io.vmms.vmmsbackend.exeption;


public class MissingUserIdException extends HttpException {
    private int httpStatus;

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
