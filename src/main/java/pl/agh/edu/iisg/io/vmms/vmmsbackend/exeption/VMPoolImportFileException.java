package pl.agh.edu.iisg.io.vmms.vmmsbackend.exeption;

public class VMPoolImportFileException extends HttpException {
    private int httpStatus;
    private String errorDescription;

    public VMPoolImportFileException(String description) {
        super("Please provide valid file!");
        this.httpStatus = 404;
        this.errorDescription = description;
    }

    @Override
    public int getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getErrorDescription() {
        return errorDescription;
    }
}

