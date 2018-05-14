package pl.agh.edu.iisg.io.vmms.vmmsbackend.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;

    @JsonProperty("name")
    private String userName;

    @JsonProperty("admin")
    private boolean isAdmin;
}
