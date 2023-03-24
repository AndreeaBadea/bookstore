package com.softserve.bookstore.repositories;

import com.softserve.bookstore.connection.ConnectionManager;
import com.softserve.bookstore.data.ManageUserData;
import com.softserve.bookstore.data.utils.UserUtility;
import com.softserve.bookstore.exceptions.UserNotFoundException;
import com.softserve.bookstore.models.Role;
import com.softserve.bookstore.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.tinylog.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

//read about life of beans in spring boot
//automatic connection creation in spring
//read about repositories in spring
//services, repositories, components, how does it works inside spring..
//database initialisation for spring
//!!!!!
//CTRL ALT L -format
@Repository
public class UserRepository implements RepositoryInterface<User> {

    public static final String INSERT_USERS = "INSERT INTO users(email, password) VALUES (?,?)";
    public static final String SELECT_USERS = "SELECT * FROM users";
    public static final String SELECT_LAST_USERS = "SELECT TOP (?) * FROM users ORDER BY id_user DESC ";
    public static final String INSERT_ORDERS = "INSERT INTO orders(id_user, date, status) VALUES(?,?,?)";

    @Autowired
    ManageUserData manageUserData;

    @Autowired
    ConnectionManager connectionManager;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    RoleRepository roleRepository;


    public List<User> findAllFromFile(String fileName) throws IOException {
        return manageUserData.readDataFromFile(fileName);
    }

    public List<User> findAll() throws SQLException {
        Connection connection = connectionManager.getConnection();
        PreparedStatement userStatement = connection.prepareStatement(SELECT_USERS);
        ResultSet resultSet = userStatement.executeQuery();
        Logger.info("Users were successfully retrived from the database.");
        return UserUtility.getUsersFromResultSet(resultSet);
    }


    public Optional<User> getUserByEmail(List<User> users, String email) {
        return users.stream()
                .filter(user -> email.equals(user.getEmail().trim()))
                .findFirst();
    }

    public void add(List<User> users) throws SQLException {
        Connection connection = connectionManager.getConnection();
        PreparedStatement userStatement = connection.prepareStatement(INSERT_USERS);
        users.forEach(user -> {
            UserUtility.addUserToBatch(userStatement, user);
        });
        userStatement.executeBatch();

        List<User> lastUsersAdded = findLastUsersAdded(users.size());
        lastUsersAdded.forEach(user -> {
            try {
                roleRepository.addRole(Role.USER.toString(), user);
                getUserByEmail(users, user.getEmail())
                        .orElseThrow(() -> new UserNotFoundException("User does not exist."));

                User currentUser = getUserByEmail(users, user.getEmail()).get();

                if (currentUser.getRoles().size() > 1) {
                    roleRepository.addRole(Role.ADMIN.toString(), user);
                }
                if (currentUser.getOrders().size() > 1) {
                    addOrdersToUser(currentUser, users.size());
                }
            } catch (UserNotFoundException | SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }


    public List<User> findLastUsersAdded(int numberOfRecords) throws SQLException {
        Connection connection = connectionManager.getConnection();
        PreparedStatement userStatement = connection.prepareStatement(SELECT_LAST_USERS);
        userStatement.setInt(1, numberOfRecords);
        ResultSet resultSet = userStatement.executeQuery();
        List<User> users = UserUtility.getUsersFromResultSet(resultSet);
        Collections.reverse(users);
        return users;
    }

    private void addOrdersToUser(User user, int numberOfRecords) throws SQLException, UserNotFoundException {
        Connection connection = connectionManager.getConnection();
        PreparedStatement orderStatement = connection.prepareStatement(INSERT_ORDERS);

        List<User> users =  findLastUsersAdded(numberOfRecords);
        User correspondingUser = users.stream()
                .filter(u -> u.getEmail().equals(user.getEmail()))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("User does not exist."));

        user.getOrders().forEach(order -> {
            try {
                orderRepository.addOrder(order, correspondingUser, orderStatement);
                orderStatement.addBatch();
            } catch (SQLException e) {
                throw new RuntimeException();
            }
        });
        orderStatement.executeBatch();
        Logger.info("Order history was successfully added for user " + user.getEmail());
    }

}
