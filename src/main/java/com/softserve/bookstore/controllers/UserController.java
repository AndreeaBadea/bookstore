package com.softserve.bookstore.controllers;

import com.softserve.bookstore.exceptions.UserNotFoundException;
import com.softserve.bookstore.models.User;
import com.softserve.bookstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    public static final String FILE_NAME = "src/main/resources/users";

    @Autowired
    UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsersFromFile() throws IOException {
        return ResponseEntity.ok(userService.getAllUsersFromFile(FILE_NAME));
    }

    @PostMapping
    public ResponseEntity<String> addAllUsers() throws SQLException, IOException, UserNotFoundException {
        userService.addUser(FILE_NAME);
        return new ResponseEntity<>("All users were added to the database.", HttpStatus.CREATED);
    }
}
