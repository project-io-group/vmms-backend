package pl.agh.edu.iisg.io.vmms.vmmsbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.http.HttpException;

@ResponseStatus(value=HttpStatus.BAD_REQUEST)
public class VMPoolImportFileException extends HttpException {
    private final int httpStatus;
    private final String errorDescription;

    public VMPoolImportFileException(String description) {
        super("Please provide valid file! - " + description);
        this.httpStatus = 400;
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

