package com.softserve.bookstore.controllers;

import com.softserve.bookstore.exceptions.UserNotFoundException;
import com.softserve.bookstore.models.ErrorResponse;
import com.softserve.bookstore.models.User;
import com.softserve.bookstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tinylog.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final String FILE_NAME = "src/main/resources/users";

    @Autowired
    private UserService userService;

    @GetMapping("/file")
    public ResponseEntity<List<User>> getAllUsersFromFile() throws IOException {
        return ResponseEntity.ok(userService.getAllUsersFromFile(FILE_NAME));
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() throws SQLException {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/last")
    public ResponseEntity<List<User>> getLastUsersAdded(@RequestParam int numberOfRecords) throws SQLException {
        return ResponseEntity.ok(userService.getLastUsersAdded(numberOfRecords));
    }

    @PostMapping
    public ResponseEntity<String> addAllUsers() throws SQLException, IOException, UserNotFoundException {
        userService.addUsers(FILE_NAME);
        return new ResponseEntity<>("All users were added to the database.", HttpStatus.CREATED);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable int userId) throws SQLException, UserNotFoundException {
        userService.deleteUser(userId);
        return new ResponseEntity<>("User was successfully deleted.", HttpStatus.OK);
    }

    @ExceptionHandler({UserNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleUserExceptions(UserNotFoundException exception) {
        Logger.error("Failed to find the requested user.", exception);
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({SQLException.class})
    public ResponseEntity<ErrorResponse> handleSqlExceptions(SQLException exception) {
        Logger.error("Failed to execute query.", exception);
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({IOException.class})
    public ResponseEntity<ErrorResponse> handleIOExceptions(IOException exception) {
        Logger.error("Failed to process information from file.", exception);
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
