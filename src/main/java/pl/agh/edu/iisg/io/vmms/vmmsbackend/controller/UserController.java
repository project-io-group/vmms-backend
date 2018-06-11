package pl.agh.edu.iisg.io.vmms.vmmsbackend.controller;


import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.dto.user.UserSignUpRequestDto;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.http.HttpException;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.http.MissingUserIdException;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.http.UserNotFoundException;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.ApplicationUser;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.security.SecurityConstants;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin("*")
@RestController
public class UserController {

    private static final String USER_ID = "userId";

    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserController(UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    @RequestMapping("/")
    public String index() {
        return "Virtual Machine Management System";
    }

    @RequestMapping(path = "/users", method = RequestMethod.GET)
    public List<ApplicationUser> getUsers() {
        return userService.getUsers();
    }

    @RequestMapping(path = "/users/{name}", method = RequestMethod.GET)
    public List<ApplicationUser> getUsers(@PathVariable String name) {
        return userService.findByNameContaining(name);
    }

    @PostMapping(SecurityConstants.SIGN_UP_URL)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public String signUp(@RequestBody UserSignUpRequestDto registerRequest) {
        // we are using email as the username fullName parameter should not be used as username
        // because it is optional. Currently fullname is not stored at all

        if (userService.find(registerRequest.getEmail()) == null) {
            ApplicationUser applicationUser = new ApplicationUser();
            applicationUser.setEmail(registerRequest.getEmail());
            applicationUser.setPassword(bCryptPasswordEncoder.encode(registerRequest.getPassword()));

            userService.save(applicationUser);
        }
        return "created";
    }

    @RequestMapping(path = "/user/delete", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public ApplicationUser deleteUser(@RequestBody Map<String, Long> request) throws HttpException {
        Long userId = Optional.ofNullable(request.get(USER_ID)).orElseThrow(MissingUserIdException::new);
        ApplicationUser applicationUser = Optional.ofNullable(userService.find(userId)).orElseThrow(UserNotFoundException::new);
        userService.delete(applicationUser);
        return applicationUser;
    }
}
