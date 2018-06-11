package pl.agh.edu.iisg.io.vmms.vmmsbackend.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class UserSignUpRequestDto {

    Boolean terms;
    String email;
    String password;
    String confirmPassword;
}
