package com.softserve.bookstore.controllers;

import com.softserve.bookstore.exceptions.UserNotFoundException;
import com.softserve.bookstore.generated.*;
import com.softserve.bookstore.models.Newsletter;
import com.softserve.bookstore.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.softserve.bookstore.generated.Role.USER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class UserControllerTest extends BaseControllerTest<User> {

    private static final String FILE_NAME = "src/main/resources/users";

    private static final int NUMBER_OF_RECORDS = 1;
    private static final String NUMBER_OF_RECORDS_LABEL = "numberOfRecords";

    private static final String USER_NOT_FOUND_MESSAGE = "Failed to find the requested user.";
    private static final String SQL_EXCEPTION_MESSAGE = "Failed to execute query.";
    private static final String IOEXCEPTION_MESSAGE = "Failed to process information from file.";
    private static final String USERS_ADDED_SUCCESS_MESSAGE = "All users were added to the database.";
    private static final String DELETE_USER_SUCCESS_MESSAGE = "User was successfully deleted.";
    private static final String SUBSCRIBE_MESSAGE = "You have successffuly subscribed to the newsletter!";
    private static final String UNSUBSCRIBE_MESSAGE = "You have unsubscribed from the newsletter!";

    private static final User firstExpectedUser = new User(1, "user1@gmail.com", "user1", Collections.emptyList(), Arrays.asList(USER, Role.ADMIN));
    private static final User secondExpectedUser = new User(2, "user2@gmail.com", "user2", Collections.emptyList(), List.of(USER));
    private static final UserDto secondExpectedUserDto = new UserDto(2, "user2@gmail.com", Collections.emptyList(), List.of(USER));

    private static final List<User> users = List.of(firstExpectedUser, secondExpectedUser);

    @MockBean
    private UserService userService;

    @MockBean
    private Newsletter newsletter;


    private static final List<OrderDto> orders = List.of(
            new OrderDto(1, Date.valueOf("2023-03-16"), Status.IN_PROCESS),
            new OrderDto(2, Date.valueOf("2023-03-16"), Status.COMPLETED)
    );

    private static final List<User> usersFromFile = List.of(
            new User(1, "andrei@gmail.com", "13e314", orders, Arrays.asList(USER, Role.ADMIN))
    );


    @Test
    void getAllUsers_Returns_OK() throws Exception {
        when(userService.getAllUsers()).thenReturn(users);
        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getAllUsers_Fails_SQL_EXCEPTION() throws Exception {
        doThrow(new SQLException(SQL_EXCEPTION_MESSAGE))
                .when(userService).getAllUsers();
        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Exception actualException = assertThrows(SQLException.class, () -> {
            userService.getAllUsers();
        });
        assertEquals(SQL_EXCEPTION_MESSAGE, actualException.getMessage());
    }

    @Test
    void getUserById_Returns_OK() throws Exception {
        when(userService.getUserById(2)).thenReturn(secondExpectedUser);
        mockMvc.perform(get("/users/{userId}", 2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(secondExpectedUserDto.getUserId()))
                .andExpect(jsonPath("$.email").value(secondExpectedUserDto.getEmail()));
    }

    @Test
    void getAllUsersFromFile_Returns_OK() throws Exception {
        when(userService.getAllUsersFromFile(FILE_NAME)).thenReturn(usersFromFile);
        mockMvc.perform(get("/users/file")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email").value("andrei@gmail.com"))
                .andExpect(jsonPath("$[0].orders[0].date").isNotEmpty());
    }

    @Test
    void getAllUsersFromFile_Fails_IOEXCEPTION() throws Exception {
        doThrow(new IOException(IOEXCEPTION_MESSAGE))
                .when(userService).getAllUsersFromFile(FILE_NAME);
        mockMvc.perform(get("/users/file")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Exception actualException = assertThrows(IOException.class, () -> {
            userService.getAllUsersFromFile(FILE_NAME);
        });
        assertEquals(IOEXCEPTION_MESSAGE, actualException.getMessage());
    }

    @Test
    void getLastUsersAdded_Returns_OK() throws Exception {
        when(userService.getLastUsersAdded(NUMBER_OF_RECORDS)).thenReturn(users);
        mockMvc.perform(get("/users/last")
                        .param(NUMBER_OF_RECORDS_LABEL, String.valueOf(NUMBER_OF_RECORDS))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void addUsers_Returns_CREATED() throws Exception {
        mvcResult = mockMvc.perform(post("/users/file")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        verify(userService, times(1)).addUsers(FILE_NAME);

        String content = mvcResult.getResponse().getContentAsString();
        assertEquals(USERS_ADDED_SUCCESS_MESSAGE, content);
    }

    @Test
    void addUsers_Fails_SQL_EXCEPTION() throws Exception {
        doThrow(new SQLException(SQL_EXCEPTION_MESSAGE))
                .when(userService).addUsers(FILE_NAME);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Exception actualException = assertThrows(SQLException.class, () -> {
            userService.addUsers(FILE_NAME);
        });
        assertEquals(SQL_EXCEPTION_MESSAGE, actualException.getMessage());
    }

    @Test
    void addUser_Returns_CREATED_user() throws Exception {
        when(userService.addUser(any())).thenReturn(secondExpectedUserDto);
        mvcResult = mockMvc.perform(post("/users")
                .content(jacksonTester.write(secondExpectedUser).getJson())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(secondExpectedUserDto.getEmail()))
                .andReturn();

        String actualUserJson = mvcResult.getResponse().getContentAsString();
        assertThat(actualUserJson).isNotEmpty();
    }

    @Test
    void deleteUser_Returns_OK() throws Exception {
        mvcResult = mockMvc.perform(delete("/users/{userId}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        verify(userService, times(1)).deleteUser(1);

        String content = mvcResult.getResponse().getContentAsString();
        assertEquals(DELETE_USER_SUCCESS_MESSAGE, content);
    }

    @Test
    void deleteUser_Returns_NOT_FOUND() throws Exception {
        doThrow(new UserNotFoundException(USER_NOT_FOUND_MESSAGE))
                .when(userService).deleteUser(1);
        mockMvc.perform(delete("/users/{userId}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        int userId = firstExpectedUser.getUserId();
        Exception actualException = assertThrows(UserNotFoundException.class, () -> {
            userService.deleteUser(userId);
        });
        assertEquals(USER_NOT_FOUND_MESSAGE, actualException.getMessage());
    }

    @Test
    void subscribeUser_Returns_OK() throws Exception {
        when(userService.getUserById(1)).thenReturn(firstExpectedUser);

        mvcResult = mockMvc.perform(put("/users/{userId}/subscribe", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        verify(newsletter, times(1)).subscribe(userService.getUserById(1));

        String content = mvcResult.getResponse().getContentAsString();
        assertEquals(SUBSCRIBE_MESSAGE, content);
    }

    @Test
    void unsubscribeUser_Returns_OK() throws Exception {
        when(userService.getUserById(1)).thenReturn(firstExpectedUser);

        mvcResult = mockMvc.perform(put("/users/{userId}/unsubscribe", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        verify(newsletter, times(1)).unsubscribe(userService.getUserById(1));

        String content = mvcResult.getResponse().getContentAsString();
        assertEquals(UNSUBSCRIBE_MESSAGE, content);
    }


}
