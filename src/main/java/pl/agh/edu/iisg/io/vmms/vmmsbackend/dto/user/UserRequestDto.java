package pl.agh.edu.iisg.io.vmms.vmmsbackend.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {

    @JsonProperty("userName")
    String userName;

    @JsonProperty("isAdmin")
    Boolean isAdmin;
}
