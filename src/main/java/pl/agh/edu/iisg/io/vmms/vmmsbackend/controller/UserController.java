package pl.agh.edu.iisg.io.vmms.vmmsbackend.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.http.HttpException;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.http.MissingUserNameException;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.http.UserNotFoundException;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.User;
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

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/")
    public String index() {
        return "Virtual Machine Management System";
    }

    @RequestMapping(path = "/users", method = RequestMethod.GET)
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @RequestMapping(path = "/users/{name}", method = RequestMethod.GET)
    public List<User> getUsers(@PathVariable String name) {
        return userService.findByNameContaining(name);
    }

    @RequestMapping(path = "/user/create", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody Map<String, String> request) throws HttpException {
        String userName = Optional.ofNullable(request.get(NAME)).orElseThrow(MissingUserNameException::new);
        return userService.save(userName);
    }

    @RequestMapping(path = "/user/delete", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public User deleteUser(@RequestBody Map<String, Long> request) throws HttpException {
        Long userId = Optional.ofNullable(request.get(USER_ID)).orElseThrow(MissingUserNameException::new);
        User user = Optional.ofNullable(userService.find(userId)).orElseThrow(UserNotFoundException::new);
        userService.delete(user);
        return user;
    }
}
