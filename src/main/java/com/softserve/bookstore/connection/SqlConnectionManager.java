package com.softserve.bookstore.connection;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tinylog.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class SqlConnectionManager implements ConnectionManager {

    private Connection connection;

    private final String url;
    private final String username;
    private final String password;

    public SqlConnectionManager(@Value("${spring.datasource.url}") String url,
                                @Value("${spring.datasource.username}") String username,
                                @Value("${spring.datasource.password}")String password
    ) {
        this.url = url;
        this.username = username;
        this.password = password;



        try{
            connection = DriverManager.getConnection(url, username, password);
            Logger.info("Connection established successfully.");

            }catch (SQLException e ){
                Logger.error("Error connecting to database..");
                e.printStackTrace();
        }
    }

    @Override
    public Connection getConnection() {
        return connection;
    }
}
