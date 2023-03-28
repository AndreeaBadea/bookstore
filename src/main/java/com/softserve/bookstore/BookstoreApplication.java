package com.softserve.bookstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.sql.SQLException;

@SpringBootApplication
public class BookstoreApplication {

	public static void main(String[] args) throws SQLException, IOException {

		 SpringApplication.run(BookstoreApplication.class, args);
	}
}
