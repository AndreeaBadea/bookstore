package com.softserve.bookstore.controllers;

import com.softserve.bookstore.exceptions.UserNotFoundException;
import com.softserve.bookstore.models.Order;
import com.softserve.bookstore.models.Role;
import com.softserve.bookstore.models.Status;
import com.softserve.bookstore.models.User;
import com.softserve.bookstore.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
public class UserControllerTest {

    private static final String FILE_NAME = "src/main/resources/users";

    public static final int NUMBER_OF_RECORDS = 1;
    public static final String NUMBER_OF_RECORDS_LABEL = "numberOfRecords";
    public static final String USER_NOT_FOUND_MESSAGE = "Failed to find the requested user.";
    public static final String SQL_EXCEPTION_MESSAGE = "Failed to execute query.";

    private static final User firstExpectedUser = new User(1, "user1@gmail.com", "user1",
            Collections.emptyList(), Arrays.asList(Role.USER, Role.ADMIN));
    private static final User secondExpectedUser = new User(2, "user2@gmail.com", "user2",
            Collections.emptyList(), List.of(Role.USER));
    private static final List<User> users = List.of(firstExpectedUser, secondExpectedUser);

    private static final Order firstOrder = new Order(1, Date.valueOf("2023-03-16"), Status.IN_PROCESS);
    private static final Order secondOrder = new Order(2, Date.valueOf("2023-03-16"), Status.COMPLETED);
    private static final List<Order> orders = List.of(firstOrder, secondOrder);

    private static final User firstExpectedUserFromFile = new User(1, "andrei@gmail.com", "13e314",
            orders, Arrays.asList(Role.USER, Role.ADMIN));
    private static final List<User> usersFromFile = List.of(firstExpectedUserFromFile);
    public static final String IOEXCEPTION_MESSAGE = "Failed to process information from file.";

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    private MvcResult mvcResult;

    @Test
    void getAllUsers_Returns_OK() throws Exception {
        when(userService.getAllUsers()).thenReturn(users);
        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
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
        mvcResult = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        verify(userService, times(1)).addUsers(FILE_NAME);

        String content = mvcResult.getResponse().getContentAsString();
        assertEquals(content, "All users were added to the database.");
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
    void deleteUser_Returns_OK() throws Exception {
        mvcResult = mockMvc.perform(delete("/users/{userId}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        verify(userService, times(1)).deleteUser(1);

        String content = mvcResult.getResponse().getContentAsString();
        assertEquals(content, "User was successfully deleted.");
    }

    @Test
    void deleteUser_Returns_NOT_FOUND() throws Exception {
        doThrow(new UserNotFoundException(USER_NOT_FOUND_MESSAGE))
                .when(userService).deleteUser(1);
        mockMvc.perform(delete("/users/{userId}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        Exception actualException = assertThrows(UserNotFoundException.class, () -> {
            userService.deleteUser(firstExpectedUser.getUserId());
        });
        assertEquals(USER_NOT_FOUND_MESSAGE, actualException.getMessage());
    }

}
