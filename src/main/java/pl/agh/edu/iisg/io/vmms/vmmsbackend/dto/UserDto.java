package pl.agh.edu.iisg.io.vmms.vmmsbackend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class UserDto {

    private Long id;

    private String userName;

    private boolean isAdmin;
}
