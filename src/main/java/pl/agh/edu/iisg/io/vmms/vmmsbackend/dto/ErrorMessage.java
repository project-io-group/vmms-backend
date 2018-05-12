package pl.agh.edu.iisg.io.vmms.vmmsbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorMessage {
    private Long timestamp;
    private Integer status;
    private String error;
    private String exception;
    private String message;
    private String path;
}
