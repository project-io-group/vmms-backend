package pl.agh.edu.iisg.io.vmms.vmmsbackend.exception;

import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.http.HttpException;

public class VMPoolImportFileException extends HttpException {
    private final int httpStatus;
    private final String errorDescription;

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

