package pl.agh.edu.iisg.io.vmms.vmmsbackend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.dto.ErrorMessage;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.http.HttpException;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(HttpException.class)
    public @ResponseBody
    ResponseEntity<ErrorMessage> roomNameBadRequest(HttpServletRequest req, HttpException ex) {
        ErrorMessage errorMessage = new ErrorMessage(new Date().getTime(), ex.getHttpStatus(), ex.getErrorDescription(),
                ex.getClass().getCanonicalName(), ex.getMessage(), req.getServletPath());
        return new ResponseEntity<>(errorMessage, HttpStatus.valueOf(ex.getHttpStatus()));
    }
}

