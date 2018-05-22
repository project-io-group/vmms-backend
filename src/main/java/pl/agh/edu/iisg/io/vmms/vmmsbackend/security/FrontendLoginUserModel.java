package pl.agh.edu.iisg.io.vmms.vmmsbackend.security;

import lombok.Data;

@Data
public class FrontendLoginUserModel {

    private final String token;
    private final String username;
}
