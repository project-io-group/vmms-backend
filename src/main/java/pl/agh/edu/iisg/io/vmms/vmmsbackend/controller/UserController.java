package pl.agh.edu.iisg.io.vmms.vmmsbackend.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.dto.UserDto;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.exception.http.*;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.User;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin("*")
@RestController
public class UserController {

    static final String USER_NAME = "name";
    static final String USER_ID = "id";
    static final String USER_ADMIN = "admin";

    static final String MAIN_ENDPOINT = "/";
    static final String USERS_ENDPOINT = "/users";
    static final String CREATE_USER_ENDPOINT = "/user/create";
    static final String DELETE_USER_ENDPOINT = "/user/delete";

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(MAIN_ENDPOINT)
    public String index() {
        return "Virtual Machine Management System";
    }

    @RequestMapping(path = USERS_ENDPOINT, method = RequestMethod.GET)
    public List<UserDto> getUsers() {
        return userService.getUsers().stream()
                .map(user -> new UserDto(user.getId(), user.getUserName(), user.isAdmin()))
                .collect(Collectors.toList());
    }

    @RequestMapping(path = USERS_ENDPOINT + "/{name}", method = RequestMethod.GET)
    public List<UserDto> getUsers(@PathVariable String name) {
        return userService.findByNameContaining(name).stream()
                .map(user -> new UserDto(user.getId(), user.getUserName(), user.isAdmin()))
                .collect(Collectors.toList());
    }

    @RequestMapping(path = CREATE_USER_ENDPOINT, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody Map<String, Object> request) throws HttpException {
        String userName = String.valueOf(Optional.ofNullable(request.get(USER_NAME)).orElseThrow(MissingUserNameException::new));
        Boolean isAdmin = (Boolean) Optional.ofNullable(request.get(USER_ADMIN)).orElse(false);
        User user = userService.save(userName, isAdmin);
        return new UserDto(user.getId(), user.getUserName(), user.isAdmin());
    }

    @RequestMapping(path = DELETE_USER_ENDPOINT, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public UserDto deleteUser(@RequestBody Map<String, Object> request) throws HttpException {
        Long userId = ((Integer) Optional.ofNullable(request.get(USER_ID)).orElseThrow(MissingUserIdException::new)).longValue();
        User user = Optional.ofNullable(userService.find(userId)).orElseThrow(UserNotFoundException::new);
        if (!user.getReservations().isEmpty()) {
            throw new NotEmptyReservationsException();
        }
        userService.delete(user);
        return new UserDto(user.getId(), user.getUserName(), user.isAdmin());
    }
}
