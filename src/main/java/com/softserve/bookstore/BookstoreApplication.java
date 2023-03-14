package com.softserve.bookstore;

import com.softserve.bookstore.connection.ConnectionManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class BookstoreApplication {



	public static void main(String[] args) {
		SpringApplication.run(BookstoreApplication.class, args);

		ConfigurableApplicationContext context = SpringApplication.run(BookstoreApplication.class, args);
		ConnectionManager connectionManager = context.getBean(ConnectionManager.class);
        connectionManager.getConnection();

	}

}
