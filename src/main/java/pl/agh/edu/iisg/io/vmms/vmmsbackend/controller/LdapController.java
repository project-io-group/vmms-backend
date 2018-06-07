package pl.agh.edu.iisg.io.vmms.vmmsbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.ldap.User;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.ldap.UserLdapService;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/ldap")
public class LdapController {

    private final UserLdapService userLdapService;

    @Autowired
    public LdapController(UserLdapService userLdapService) {
        this.userLdapService = userLdapService;
    }

    @RequestMapping(path = "/users", method = RequestMethod.GET)
    public List< User> getUsers() {
        return userLdapService.getUsers();
    }

    @RequestMapping(path = "/admins", method = RequestMethod.GET)
    public List< User> getAdmins() {
        return userLdapService.getUsers();
    }
}
