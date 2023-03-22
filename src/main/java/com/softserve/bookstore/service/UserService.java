package com.softserve.bookstore.service;

import com.mysql.cj.log.Log;
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
    UserRepository userRepository;

    public List<User> getAllUsersFromFile(String fileName) throws IOException {
        List<User> users = userRepository.findAllFromFile(fileName);
        Logger.info("All users were retrived from provided file.");
        return users;
    }

    public void addUser(String fileName) throws SQLException, IOException {
        List<User> users = getAllUsersFromFile(fileName);
        userRepository.addUsers(users);
        Logger.info("Users successfully added to the database.");
    }

}
