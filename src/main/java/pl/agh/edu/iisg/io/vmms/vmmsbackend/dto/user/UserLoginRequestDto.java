package pl.agh.edu.iisg.io.vmms.vmmsbackend.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserLoginRequestDto {

    Boolean rememberMe;
    String email;
    String password;
}
