package com.softserve.bookstore;

import com.softserve.bookstore.connection.ConnectionManager;
import com.softserve.bookstore.data.ManageUserData;
import com.softserve.bookstore.models.User;
import com.softserve.bookstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@SpringBootApplication
public class BookstoreApplication {

	public static void main(String[] args) throws SQLException, IOException {

		ConfigurableApplicationContext context = SpringApplication.run(BookstoreApplication.class, args);
//		ConnectionManager connectionManager = context.getBean(ConnectionManager.class);
//        connectionManager.getConnection();

//		Order o1 = new Order(1, LocalDateTime.now(), Status.IN_PROCESS);
//		Order o12 = new Order(2, LocalDateTime.now(), Status.COMPLETED);
//		List<Order> ordersUser1 = new ArrayList<>();
//		ordersUser1.add(o1);
//		ordersUser1.add(o12);
//
//		List<Role> rolesU1 = new ArrayList<>();
//		rolesU1.add(Role.ADMIN);
//		rolesU1.add(Role.USER);
//
//		User u1 = new User(1, "andrei@gmail.com", "13e314", ordersUser1, rolesU1);
//
//		try{
//			FileWriter writer = new FileWriter("exemplu.csv");
//			writer.write(u1.getId() + "," + u1.getEmail() + "," + u1.getPassword() + "," + u1.getOrders() + "," +u1.getRoles());
//			writer.close();
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}


		//ManageUserData manageUserData = new ManageUserData();

		//List<User> userList = manageUserData.readDataFromFile("src/main/resources/users");




	}

}
