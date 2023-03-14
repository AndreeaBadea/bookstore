package com.softserve.bookstore.connection;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
            connection =  DriverManager.getConnection(url);
            System.out.println("Connection successful.");
        }catch (SQLException e){
            System.out.println("Connection to database failed.");
        }
    }

    @Override
    public Connection getConnection() {
        return connection;
    }
}
