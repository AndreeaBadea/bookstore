package com.softserve.bookstore.repositories;

import com.softserve.bookstore.connection.ConnectionManager;
import com.softserve.bookstore.exceptions.ProcessOrderException;
import com.softserve.bookstore.exceptions.UserNotFoundException;
import com.softserve.bookstore.generated.OrderDto;
import com.softserve.bookstore.generated.Role;
import com.softserve.bookstore.generated.User;
import com.softserve.bookstore.generated.UserDto;
import com.softserve.bookstore.models.dtos.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.tinylog.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.*;
import java.util.*;


@Repository
public class UserRepository {

    public static final String SELECT_USERS = "SELECT * FROM users";
    public static final String SELECT_LAST_USERS = "SELECT TOP (?) * FROM users ORDER BY id_user DESC ";
    public static final String INSERT_USER = "INSERT INTO users(email, password) VALUES (?,?)";
    public static final String INSERT_USERS_ROLES = "INSERT INTO users_roles(id_user, id_role) VALUES(?,?)";
    public static final String SELECT_USERS_ROLES = "SELECT * FROM users_roles WHERE id_user = ?";
    public static final String INSERT_ORDERS = "INSERT INTO orders(id_user, date, status) VALUES(?,?,?)";
    public static final String DELETE_USER = "DELETE FROM users WHERE id_user = ?";
    public static final String DELETE_ROLE = "DELETE FROM users_roles WHERE id_user = ?";
    public static final String DELETE_ORDER = "DELETE FROM orders WHERE id_user = ?";
    public static final String SUBSCRIBE_USER = "UPDATE users SET is_subscribed = 'true' WHERE id_user = ? ";
    public static final String UNSUBSCRIBE_USER = "UPDATE users SET is_subscribed = 'false' WHERE id_user = ? ";

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
        return UserUtil.getUsersFromResultSet(resultSet);
    }

    public List<User> findLastUsersAdded(int numberOfRecords) throws SQLException {
        PreparedStatement userStatement = connection.prepareStatement(SELECT_LAST_USERS);
        userStatement.setInt(1, numberOfRecords);
        ResultSet resultSet = userStatement.executeQuery();
        List<User> users = UserUtil.getUsersFromResultSet(resultSet);
        Collections.reverse(users);
        return users;
    }

    private void addUserToBatch(PreparedStatement userStatement, User user) throws SQLException {
        userStatement.setString(1, user.getEmail());
        userStatement.setString(2, user.getPassword());
        userStatement.addBatch();
    }

    public Optional<User> getUserById(int userId) throws SQLException {
        Optional<User> retrivedUser = findAll().stream()
                .filter(user -> userId == user.getUserId())
                .findFirst();

        PreparedStatement roleStatement = connection.prepareStatement(SELECT_USERS_ROLES);
        roleStatement.setInt(1, userId);
        ResultSet resultSet = roleStatement.executeQuery();

        List<Role> roles = UserUtil.getRolesFromResultSet(resultSet);
        retrivedUser.ifPresent(user -> user.setRoles(roles));
        return retrivedUser;
    }

    public Optional<User> getUserByEmail(List<User> users, String email) {
        return users.stream()
                .filter(user -> email.equals(user.getEmail().trim()))
                .findFirst();
    }

    @Transactional
    public void addUsers(List<User> users) throws SQLException, UserNotFoundException {
        PreparedStatement userStatement = connection.prepareStatement(INSERT_USER);
        for (User user : users) {
            addUserToBatch(userStatement, user);
        }
        userStatement.executeBatch();
        List<User> lastUsersAdded = findLastUsersAdded(users.size());
        for (User user : lastUsersAdded) {
            addRole(Role.USER, user);
            getUserByEmail(users, user.getEmail())
                    .orElseThrow(() -> new UserNotFoundException("User with this email does not exist!"));

            User currentUser = getUserByEmail(users, user.getEmail()).get();

            if (currentUser.getRoles().size() > 1) {
                addRole(Role.ADMIN, user);
            }
            if (!user.getOrders().isEmpty() && user.getOrders().stream().allMatch(Objects::nonNull)) {
                addOrdersToUser(currentUser, users.size());
            }
        }
    }

    @Transactional
    public UserDto addUser(User user) throws SQLException {
        PreparedStatement userStatement = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS);
        userStatement.setString(1, user.getEmail());
        userStatement.setString(2, user.getPassword());
        userStatement.executeUpdate();

        ResultSet generatedKeys = userStatement.getGeneratedKeys();
        if (generatedKeys.next()) {
            int userId = generatedKeys.getInt(1);
            user.setUserId(userId);
        }

        for (Role role : user.getRoles()) {
            addRole(role, user);
        }
        return UserMapper.toUserDto(user);
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

    private void addOrder(OrderDto order, User user, PreparedStatement orderStatement) throws SQLException {
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


    @Transactional
    public int deleteUserById(int userId) throws SQLException, UserNotFoundException {
        findAll().stream()
                .filter(user -> user.getUserId() == userId)
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("User does not exist!"));

        deleteUserRoles(userId);
        Logger.info("All the roles for user id {} was successfully deleted.", userId);

        deleteUserOrders(userId);
        Logger.info("All the orders for user id {} was successfully deleted.", userId);

        PreparedStatement userStatement = connection.prepareStatement(DELETE_USER);
        userStatement.setInt(1, userId);

        int affectedRows = userStatement.executeUpdate();
        Logger.info("User id {} and the information related to this user were successfully deleted.", userId);
        return affectedRows;
    }

    private int deleteUserOrders(int userId) throws SQLException {
        PreparedStatement orderStatement = connection.prepareStatement(DELETE_ORDER);
        orderStatement.setInt(1, userId);
        return orderStatement.executeUpdate();
    }


    private int deleteUserRoles(int userId) throws SQLException {
        PreparedStatement roleStatement = connection.prepareStatement(DELETE_ROLE);
        roleStatement.setInt(1, userId);
        return roleStatement.executeUpdate();
    }


    public void subscribeToNewsletter(int userId) throws SQLException {
        PreparedStatement subscriptionStatement = connection.prepareStatement(SUBSCRIBE_USER);
        subscriptionStatement.setInt(1, userId);
        subscriptionStatement.executeUpdate();
    }

    public void unsubscribeFromNewsletter(int userId) throws SQLException {
        PreparedStatement subscriptionStatement = connection.prepareStatement(UNSUBSCRIBE_USER);
        subscriptionStatement.setInt(1, userId);
        subscriptionStatement.executeUpdate();
    }


    @PreDestroy
    public void closeConnection() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}

class UserUtil {

    private UserUtil() {
    }

    public static List<User> getUsersFromResultSet(ResultSet resultSet) throws SQLException {
        List<User> users = new ArrayList<>();
        while (resultSet.next()) {
            int userId = resultSet.getInt(User.FIELD_USER_ID);
            String email = resultSet.getString(User.FIELD_EMAIL).trim();
            String password = resultSet.getString(User.FIELD_PASSWORD).trim();
            Boolean isSubscribed = resultSet.getBoolean(User.FIELD_SUBSCRIPTION);
            User user = new User(userId, email, password, isSubscribed);
            users.add(user);
        }
        return users;
    }

    public static List<Role> getRolesFromResultSet(ResultSet resultSet) throws SQLException {
        Map<Integer, Role> roleIds = new HashMap<>();
        roleIds.put(1, Role.USER);
        roleIds.put(2, Role.ADMIN);

        List<Role> roles = new ArrayList<>();
        while (resultSet.next()) {
            int roleId = resultSet.getInt("id_role");
            roles.add(roleIds.get(roleId));
        }
        return roles;
    }

}

