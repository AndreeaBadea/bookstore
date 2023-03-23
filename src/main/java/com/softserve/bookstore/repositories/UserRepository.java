package com.softserve.bookstore.repositories;

import com.softserve.bookstore.connection.ConnectionManager;
import com.softserve.bookstore.models.Order;
import com.softserve.bookstore.models.Role;
import com.softserve.bookstore.models.User;
import com.softserve.bookstore.data.ManageUserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.tinylog.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class UserRepository {

    public static final String INSERT_USERS = "INSERT INTO users(email, password) VALUES (?,?)";
    public static final String SELECT_USERS = "SELECT * FROM users";

    public static final String SELECT_LAST_USERS = "SELECT TOP 2 * FROM users ORDER BY id_user DESC ";


    public static final String INSERT_USERS_ROLES = "INSERT INTO users_roles(id_user, id_role) VALUES(?,?)";
    public static final String INSERT_ORDERS = "INSERT INTO orders(id_user, date, status) VALUES(?,?,?)";


    @Autowired
    ManageUserData manageUserData;

    @Autowired
    ConnectionManager connectionManager;


    public List<User> findAllFromFile(String fileName) throws IOException {
        return manageUserData.readDataFromFile(fileName);
    }

    public List<User> findAll() throws SQLException {
        Connection connection = connectionManager.getConnection();
        PreparedStatement userStatement = connection.prepareStatement(SELECT_USERS);
        ResultSet resultSet = userStatement.executeQuery();
        Logger.info("Users were successfully retrived from the database.");

        return Stream.generate(() -> getNextUser(resultSet))
                .takeWhile(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

//        int id = 1;
//        return Stream.generate(() -> i)
//                .takeWhile(() -> isNext(resultSet))
//                .map()
//                .map(Optional::get)
//                .collect(Collectors.toList());
    }

    public List<User> findLastUsersAdded(int numberOfRecord) throws SQLException {
        Connection connection = connectionManager.getConnection();
        PreparedStatement userStatement = connection.prepareStatement(SELECT_LAST_USERS);
       // userStatement.setInt(1, numberOfRecord);
        ResultSet resultSet = userStatement.executeQuery();
        List<User> users = new ArrayList<>();
        while(resultSet.next()){
            int userId = resultSet.getInt("id_user");
            String email = resultSet.getString("email").trim();
            String password = resultSet.getString("password").trim();
            User user = new User(userId, email, password);
            users.add(user);
        }
        Collections.reverse(users);
        return users;
    }

//    private static boolean isNext(ResultSet resultSet) throws SQLException {
//        try {
//            return resultSet.next();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }

    private Optional<User> getNextUser(ResultSet resultSet){
        try{
            if(resultSet.next()){
                int userId = resultSet.getInt("id_user");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                return Optional.of(new User(userId, email, password));
            }else{
                return Optional.empty();
            }
        } catch (SQLException e){
            return Optional.empty();
        }
    }

    public User getUserByEmail(List<User> users, String email){
        for(User user : users){
            if(email.equals(user.getEmail().trim())){
                return user;
            }
        }
        //fac cu optional
        return null;
    }


    public void addUsers(List<User> users) throws SQLException {
        Connection connection = connectionManager.getConnection();
        PreparedStatement userStatement = connection.prepareStatement(INSERT_USERS);
        users.forEach(user -> {
            addToBatch(userStatement, user);
        });
        userStatement.executeBatch();

        List<User> lastUsersAdded = findLastUsersAdded(2);

        for (int i = 0; i < lastUsersAdded.size(); i++) {

            addRole(Role.USER.toString(), lastUsersAdded.get(i));
            User user = getUserByEmail(users, lastUsersAdded.get(i).getEmail());

            if (user.getRoles().size() > 1) {
                addRole(Role.ADMIN.toString(), lastUsersAdded.get(i));
            }

            if (users.get(i).getOrders().size() > 1) {
                addOrdersToUser(users.get(i));
            }
        }
    }





    private static void addToBatch(PreparedStatement userStatement, User user) {
        try {
            userStatement.setString(1, user.getEmail());
            userStatement.setString(2, user.getPassword());
            userStatement.addBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addRole(String roleName, User user) throws SQLException {
        Connection connection = connectionManager.getConnection();

        Map<String, Integer> roleIds = new HashMap<>();
        roleIds.put(Role.USER.toString(), 1);
        roleIds.put(Role.ADMIN.toString(), 2);

        int roleId = Optional.ofNullable(roleIds.get(roleName))
                .orElseThrow(() -> new IllegalArgumentException("Invalid role name."));

        List<Role> roles = new ArrayList<>();
        roles.add(Role.valueOf(roleName));
        user.setRoles(roles);

        PreparedStatement roleStatement = connection.prepareStatement(INSERT_USERS_ROLES);

        roleStatement.setInt(1, user.getUserId());
        roleStatement.setInt(2, roleId);
        roleStatement.executeUpdate();
        Logger.info("User " + user.getEmail() + " has now role " + user.getRoles());

    }

    public void addOrder(Order order, User user, PreparedStatement orderStatement) throws SQLException {
        orderStatement.setInt(1, user.getUserId());
        orderStatement.setDate(2, order.getDate());
        orderStatement.setString(3, order.getStatus().toString());
    }

    public void addOrdersToUser(User user) throws SQLException {
        Connection connection = connectionManager.getConnection();

        PreparedStatement orderStatement = connection.prepareStatement(INSERT_ORDERS);

        List<User> users = findLastUsersAdded(2);
        User aaa = null;
        for(User userA : users){
            if (userA.getEmail().equals(user.getEmail())){
                aaa = userA;
            }
        }

        for(int i = 0; i < user.getOrders().size(); i++){
            addOrder(user.getOrders().get(i), aaa, orderStatement);
            System.out.println("comanda" + user.getOrders().get(i) + " pt userul " + aaa.getEmail());
            orderStatement.addBatch();
        }
        orderStatement.executeBatch();
        Logger.info("Order history was successfully added for user " + user.getEmail());
    }

}
