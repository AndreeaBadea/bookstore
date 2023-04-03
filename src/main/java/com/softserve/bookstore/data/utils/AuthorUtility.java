package com.softserve.bookstore.data.utils;

import com.softserve.bookstore.models.Author;

import java.sql.PreparedStatement;
import java.sql.SQLException;

//TODO just follow the principal of Class+Util
public class AuthorUtility {

    public static void addToBatch(PreparedStatement statement, Author author) {
        try {

            statement.setString(1, author.getFirstName());
            statement.setString(2, author.getLastName());
            statement.addBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
