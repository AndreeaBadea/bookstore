package com.softserve.bookstore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import java.sql.DriverManager;
import java.sql.Statement;

@SpringBootApplication
public class BookstoreApplication {

//	@Autowired
//	private static Environment environment;
//
//
//	@Value("${spring.datasource.url}")
//	private static String URL;
//
//	@Value("${spring.datasource.username}")
//	private static String username;
//
//	@Value("${spring.datasource.password")
//	private static String password;

	@Autowired
	private static Connection connection;

	public static void main(String[] args) {
		SpringApplication.run(BookstoreApplication.class, args);


		System.out.println(connection.getConfigValue("spring.datasource.username"));


//		Connection con = DriverManager.getConnection(jdbcUrl, username, password);
//		Statement st = con.createStatement();
//		st.executeUpdate("CREATE TABLE employees(no number, name varchar2(10), salary number(10,2), address varchar2(10)");
//		System.out.println("Table created successfully");
//		con.close();

	}

}
