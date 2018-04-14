package pl.agh.edu.iisg.io.vmms.vmmsbackend.exeption;

public abstract class HttpException extends Exception {

    public HttpException(String message) {
        super(message);
    }

    public abstract int getHttpStatus();

    public abstract String getErrorDescription();
}