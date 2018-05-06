package pl.agh.edu.iisg.io.vmms.vmmsbackend.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.junit.*;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.dto.UserDto;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.model.User;
import pl.agh.edu.iisg.io.vmms.vmmsbackend.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static io.restassured.path.json.JsonPath.from;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static pl.agh.edu.iisg.io.vmms.vmmsbackend.controller.UserController.*;

@RunWith(Theories.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerIntegrationTest {

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @DataPoint
    public static Boolean ADMIN = true;
    @DataPoint
    public static Boolean NOT_ADMIN = false;

    @LocalServerPort
    private int port;

    @Autowired
    private UserService userService;

    @Before
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    public void shouldReturnUsers() {
        //given
        RequestSpecification request = given()
                .accept(ContentType.JSON);

        //when
        Response response = request.when()
                .get(USERS_ENDPOINT);
        //then
        response.then()
                .statusCode(HttpStatus.SC_OK);

        Assert.assertEquals(userService.getUsers().stream()
                .map(user -> new UserDto(user.getId(), user.getUserName(), user.isAdmin()))
                .collect(Collectors.toList()), extractUsersList(response));
    }

    @Theory
    public void shouldReturnCreatedUser(Boolean isAdmin) {
        //given
        String userName = generateRandomString();
        Long userId = userService.save(userName, isAdmin).getId();

        RequestSpecification request = given()
                .accept(ContentType.JSON);

        //when
        Response response = request.when()
                .get(USERS_ENDPOINT);

        //then
        response.then()
                .statusCode(HttpStatus.SC_OK);

        Assert.assertNotNull(userId);
        assertThat(extractUsersList(response), hasItem(new UserDto(userId, userName, isAdmin)));
    }

    @Theory
    public void shouldCreateUser(Boolean isAdmin) {
        //given
        int beforeSize = userService.getUsers().size();
        String userName = generateRandomString();

        RequestSpecification request = given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON);

        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put(USER_NAME, userName);
        requestParams.put(USER_ADMIN, isAdmin);

        //when
        Response response = request.when()
                .body(requestParams)
                .post(CREATE_USER_ENDPOINT);

        //then
        response.then()
                .statusCode(HttpStatus.SC_CREATED)
                .body(USER_NAME, is(userName))
                .body(USER_ID, any(Integer.class))
                .body(USER_ADMIN, is(isAdmin));

        Long userId = response.then()
                .extract().jsonPath().getLong(USER_ID);

        List<UserDto> users = userService.getUsers().stream()
                .map(user -> new UserDto(user.getId(), user.getUserName(), user.isAdmin()))
                .collect(Collectors.toList());
        Assert.assertEquals(users.size(), beforeSize + 1);
        assertThat(users, hasItem(new UserDto(userId, userName, isAdmin)));
    }

    @Theory
    public void shouldDeleteUser(Boolean isAdmin) {
        //given
        String userName = generateRandomString();
        User user = userService.save(userName, isAdmin);
        Long userId = user.getId();
        int beforeSize = userService.getUsers().size();

        RequestSpecification request = given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON);

        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put(USER_ID, userId);

        //when
        Response response = request.when()
                .body(requestParams)
                .post(DELETE_USER_ENDPOINT);

        //then
        response.then()
                .statusCode(HttpStatus.SC_OK)
                .body(USER_NAME, is(userName))
                .body(USER_ID, is(userId.intValue()))
                .body(USER_ADMIN, is(isAdmin));

        List<UserDto> users = userService.getUsers().stream()
                .map(u -> new UserDto(u.getId(), u.getUserName(), u.isAdmin()))
                .collect(Collectors.toList());
        Assert.assertEquals(users.size(), beforeSize - 1);
        Assert.assertFalse(users.contains(new UserDto(userId, userName, isAdmin)));
    }

    private List<UserDto> extractUsersList(Response response) {
        return from(response.asString()).getList("$", UserDto.class);
    }

    private String generateRandomString() {
        return RandomStringUtils.random(20, true, false);
    }
}
