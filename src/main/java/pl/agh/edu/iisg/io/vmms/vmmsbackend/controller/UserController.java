package pl.agh.edu.iisg.io.vmms.vmmsbackend.controller;


import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.dto.user.UserLoginRequestDto;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.dto.user.UserSignUpRequestDto;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exeption.HttpException;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exeption.MissingUserIdException;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exeption.UserNotFoundException;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.ApplicationUser;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.security.SecurityConstants;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin("*")
@RestController
public class UserController {

    private static final String NAME = "name";
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
    public void signUp(@RequestBody UserSignUpRequestDto registerRequest) {
        // we are using email as the username fullName parameter should not be used as username
        // because it is optional. Currently fullname is not stored at all
        ApplicationUser applicationUser = new ApplicationUser();
        applicationUser.setUserName(registerRequest.getEmail());
        applicationUser.setEmail(registerRequest.getEmail());
        applicationUser.setPassword(bCryptPasswordEncoder.encode(registerRequest.getPassword()));

        userService.save(applicationUser);
    }

    @PostMapping(SecurityConstants.LOGIN_URL)
    public void login(@RequestBody UserLoginRequestDto loginRequest) {
        //TODO implement the logic of signing in (loginRequest have all JSON fields)
    }

    @RequestMapping(path = "/user/create", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationUser createUser(@RequestBody Map<String, String> request) throws HttpException {
        String userName = Optional.ofNullable(request.get(NAME)).orElseThrow(MissingUserIdException::new);
        return userService.save(userName);
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
