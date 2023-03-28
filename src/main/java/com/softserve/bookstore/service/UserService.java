package com.softserve.bookstore.service;

import com.softserve.bookstore.data.ManageUserData;
import com.softserve.bookstore.exceptions.UserNotFoundException;
import com.softserve.bookstore.models.User;
import com.softserve.bookstore.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tinylog.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ManageUserData manageUserData;

    public List<User> getAllUsersFromFile(String fileName) throws IOException {
        List<User> users = manageUserData.readUserDataFromFile(fileName);
        Logger.info("All users were retrived from provided file.");
        return users;
    }

    public void addUser(String fileName) throws SQLException, IOException, UserNotFoundException {
        List<User> users = getAllUsersFromFile(fileName);
        userRepository.addUsers(users);
        Logger.info("Users successfully added to the database.");
    }

}
