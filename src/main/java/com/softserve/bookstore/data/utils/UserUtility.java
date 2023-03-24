package com.softserve.bookstore.data.utils;

import com.softserve.bookstore.models.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserUtility {

    public static List<User> getUsersFromResultSet(ResultSet resultSet) throws SQLException {
        List<User> users = new ArrayList<>();
        while (resultSet.next()) {
            int userId = resultSet.getInt("id_user");
            String email = resultSet.getString("email").trim();
            String password = resultSet.getString("password").trim();
            User user = new User(userId, email, password);
            users.add(user);
        }
        return users;
    }

    public static void addUserToBatch(PreparedStatement userStatement, User user) {
        try {
            userStatement.setString(1, user.getEmail());
            userStatement.setString(2, user.getPassword());
            userStatement.addBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
