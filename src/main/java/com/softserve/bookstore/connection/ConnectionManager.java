package com.softserve.bookstore.connection;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionManager {

    Connection getConnection() throws SQLException;

}
