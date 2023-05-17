package com.softserve.bookstore.service;

import com.softserve.bookstore.data.ManageUserData;
import com.softserve.bookstore.exceptions.UserNotFoundException;
import com.softserve.bookstore.generated.User;
import com.softserve.bookstore.generated.UserDto;
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

    public List<User> getAllUsers() throws SQLException {
        List<User> users = userRepository.findAll();
        Logger.info("All users were retrived from the database.");
        return users;
    }

    public List<User> getLastUsersAdded(int numberOfRecords) throws SQLException {
        List<User> users = userRepository.findLastUsersAdded(numberOfRecords);
        Logger.info("Last {} users added were retrived from the database", numberOfRecords);
        return users;
    }

    public User getUserById(int userId) throws SQLException, UserNotFoundException {
        return userRepository.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("User does not exist!"));
    }

    public User getUserByEmail(String email) throws SQLException, UserNotFoundException {
        List<User> users = getAllUsers();
        return userRepository.getUserByEmail(users, email)
                .orElseThrow(()-> new UserNotFoundException("User does not exist!"));
    }

    public void addUsers(String fileName) throws SQLException, IOException, UserNotFoundException {
        List<User> users = getAllUsersFromFile(fileName);
        userRepository.addUsers(users);
        Logger.info("Users successfully added to the database.");
    }

    public UserDto addUser(User user) throws SQLException {
        UserDto userAdded = userRepository.addUser(user);
        Logger.info("User id {} successfully added to the database.", userAdded.getUserId());
        return userAdded;
    }

    public void deleteUser(int userId) throws SQLException, UserNotFoundException {
        int noRowsAffected = userRepository.deleteUserById(userId);
        Logger.info("Number of rows affected after deletion: {}", noRowsAffected);
    }

}
