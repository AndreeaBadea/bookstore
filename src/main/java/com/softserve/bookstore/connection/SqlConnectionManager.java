package com.softserve.bookstore.connection;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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
            try(Connection connection = DriverManager.getConnection(url, username, password)){
                System.out.println("Connection established successfully.");
                Statement statement = connection.createStatement();
                String query = "CREATE TABLE test " + "(ID int not NULL," + "name VARCHAR(20))";
                statement.executeUpdate(query);
                System.out.println("Table created.");
            }
            }catch (SQLException e ){
                System.out.println("Error connecting");   e.printStackTrace();
        }
    }

    @Override
    public Connection getConnection() {
        return connection;
    }
}
