package com.softserve.bookstore.repositories;

import com.softserve.bookstore.models.Order;
import com.softserve.bookstore.models.User;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class OrderRepository {

    public void addOrder(Order order, User user, PreparedStatement orderStatement) throws SQLException {
        orderStatement.setInt(1, user.getUserId());
        orderStatement.setDate(2, order.getDate());
        orderStatement.setString(3, order.getStatus().toString());
    }


}
