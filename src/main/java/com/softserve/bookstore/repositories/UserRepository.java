package com.softserve.bookstore.repositories;

import com.softserve.bookstore.connection.ConnectionManager;
import com.softserve.bookstore.exceptions.UserNotFoundException;
import com.softserve.bookstore.models.Order;
import com.softserve.bookstore.models.Role;
import com.softserve.bookstore.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.tinylog.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class UserRepository {

    public static final String INSERT_USERS = "INSERT INTO users(email, password) VALUES (?,?)";
    public static final String SELECT_USERS = "SELECT * FROM users";
    public static final String SELECT_LAST_USERS = "SELECT TOP (?) * FROM users ORDER BY id_user DESC ";
    public static final String INSERT_USERS_ROLES = "INSERT INTO users_roles(id_user, id_role) VALUES(?,?)";
    public static final String INSERT_ORDERS = "INSERT INTO orders(id_user, date, status) VALUES(?,?,?)";

    @Autowired
    private ConnectionManager connectionManager;

    private Connection connection;

    @PostConstruct
    public void init() throws SQLException {
        connection = connectionManager.getConnection();
    }

    public List<User> findAll() throws SQLException {
        PreparedStatement userStatement = connection.prepareStatement(SELECT_USERS);
        ResultSet resultSet = userStatement.executeQuery();
        Logger.info("Users were successfully retrived from the database.");
        return UserUtility.getUsersFromResultSet(resultSet);
    }

    public List<User> findLastUsersAdded(int numberOfRecords) throws SQLException {
        PreparedStatement userStatement = connection.prepareStatement(SELECT_LAST_USERS);
        userStatement.setInt(1, numberOfRecords);
        ResultSet resultSet = userStatement.executeQuery();
        List<User> users = UserUtility.getUsersFromResultSet(resultSet);
        Collections.reverse(users);
        return users;
    }

    private void addUserToBatch(PreparedStatement userStatement, User user) throws SQLException {
        userStatement.setString(1, user.getEmail());
        userStatement.setString(2, user.getPassword());
        userStatement.addBatch();
    }

    public Optional<User> getUserByEmail(List<User> users, String email) {
        return users.stream()
                .filter(user -> email.equals(user.getEmail().trim()))
                .findFirst();
    }

    public void addUsers(List<User> users) throws SQLException, UserNotFoundException {
        PreparedStatement userStatement = connection.prepareStatement(INSERT_USERS);
        for (User user : users) {
            addUserToBatch(userStatement, user);
        }
        userStatement.executeBatch();

        List<User> lastUsersAdded = findLastUsersAdded(users.size());
        for (User user : lastUsersAdded) {
            addRole(Role.USER, user);
            getUserByEmail(users, user.getEmail())
                    .orElseThrow(() -> new UserNotFoundException("User with email"));

            User currentUser = getUserByEmail(users, user.getEmail()).get();

            if (currentUser.getRoles().size() > 1) {
                addRole(Role.ADMIN, user);
            }
            if (currentUser.getOrders().size() > 1) {
                addOrdersToUser(currentUser, users.size());
            }
        }
    }

    private void addRole(Role role, User user) throws SQLException {
        Map<Role, Integer> roleIds = new HashMap<>();
        roleIds.put(Role.USER, 1);
        roleIds.put(Role.ADMIN, 2);

        int roleId = Optional.ofNullable(roleIds.get(role))
                .orElseThrow(() -> new IllegalArgumentException("Invalid role name."));

        List<Role> roles = new ArrayList<>();
        roles.add(role);
        user.setRoles(roles);

        PreparedStatement roleStatement = connection.prepareStatement(INSERT_USERS_ROLES);
        roleStatement.setInt(1, user.getUserId());
        roleStatement.setInt(2, roleId);
        roleStatement.executeUpdate();
        Logger.info("User {} has now role {}. ", user.getEmail(), user.getRoles());
    }

    private void addOrder(Order order, User user, PreparedStatement orderStatement) throws SQLException {
        orderStatement.setInt(1, user.getUserId());
        orderStatement.setDate(2, order.getDate());
        orderStatement.setString(3, order.getStatus().toString());
    }

    private void addOrdersToUser(User user, int numberOfRecords) throws SQLException, UserNotFoundException {
        PreparedStatement orderStatement = connection.prepareStatement(INSERT_ORDERS);

        List<User> users = findLastUsersAdded(numberOfRecords);
        User correspondingUser = users.stream()
                .filter(u -> u.getEmail().equals(user.getEmail()))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("User does not exist."));

        user.getOrders().forEach(order -> {
            try {
                addOrder(order, correspondingUser, orderStatement);
                orderStatement.addBatch();
            } catch (SQLException e) {
                throw new RuntimeException("Exception occured while adding order to user, SQLException : " + e.getMessage());
            }
        });
        orderStatement.executeBatch();
        Logger.info("Order history was successfully added for user {}.", user.getEmail());
    }

    @PreDestroy
    public void closeConnection() throws SQLException {
        if(connection != null){
            connection.close();
        }
    }
}

class UserUtility {

    public static List<User> getUsersFromResultSet(ResultSet resultSet) throws SQLException {
        List<User> users = new ArrayList<>();
        while(resultSet.next()){
            int userId = resultSet.getInt(User.FIELD_USER_ID);
            String email = resultSet.getString(User.FIELD_EMAIL).trim();
            String password = resultSet.getString(User.FIELD_PASSWORD).trim();
            User user = new User(userId, email, password);
            users.add(user);
        }
        return users;
    }
}

