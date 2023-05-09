package com.softserve.bookstore.data;

import com.softserve.bookstore.generated.OrderDto;
import com.softserve.bookstore.generated.Role;
import com.softserve.bookstore.generated.Status;
import com.softserve.bookstore.generated.User;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

import static com.softserve.bookstore.data.ManageUserData.*;

@Component
public class ManageUserData {

    public static final int SPLIT_STRING_LIMIT = 5;
    public static final String ORDER_ID = "orderId";
    public static final String DATE = "date";
    public static final String STATUS = "status";

    private List<User> userList = new ArrayList<>();

    public List<User> readUserDataFromFile(String file) throws IOException {
        try (FileReader fileReader = new FileReader(file);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {

             String line;
             List<OrderDto> orders;
             List<Role> roles;

            while ((line = bufferedReader.readLine()) != null && line.length() != 0) {
                String[] userDetails = line.split(",(?=[^\\s])", SPLIT_STRING_LIMIT);

                int id = Integer.parseInt(userDetails[0]);
                String email = userDetails[1];
                String password = userDetails[2];

                String ordersString = userDetails[3].substring(1, userDetails[3].length() - 1);
                orders = DataParser.parseOrders(ordersString);

                String rolesString = userDetails[4].substring(1, userDetails[4].length() - 1);
                roles = DataParser.parseRoles(rolesString);

                User user = new User(id, email, password, orders, roles);
                userList.add(user);
            }
        }
        return userList;
    }

}

class DataParser{

    public static List<OrderDto> parseOrders(String ordersString) {
        List<OrderDto> finalOrders = new ArrayList<>();
        List<String> ordersList = Arrays.stream(ordersString.split("(?<=\\}),\\s"))
                .map(String::new)
                .collect(Collectors.toList());

        for (String order : ordersList) {
            finalOrders.add(parseOrder(order));
        }
        return finalOrders;
    }

    public static OrderDto parseOrder(String orderString) {
        OrderDto order = null;

        if (orderString.startsWith("Order{") && orderString.endsWith("}")) {
            orderString = orderString.substring(6, orderString.length() - 1);
            String[] orderDetails = orderString.split("\\},\\s*\\{");

            for (String orderDetail : orderDetails) {
                orderDetail = orderDetail.replaceAll("[\\[\\]{}]", "");
                String[] attributes = orderDetail.split(",\\s*");
                Map<String, String> orderAttributes = new HashMap<>();

                for (String attribute : attributes) {
                    String[] keyValue = attribute.split("=");
                    orderAttributes.put(keyValue[0], keyValue[1]);
                }
                int orderId = Integer.parseInt(orderAttributes.get(ORDER_ID));
                LocalDateTime localDateTime = LocalDateTime.parse(orderAttributes.get(DATE));
                java.sql.Date sqlDate = java.sql.Date.valueOf(localDateTime.toLocalDate());
                Status status = Status.valueOf(orderAttributes.get(STATUS));

                order = new OrderDto(orderId, sqlDate, status);
            }
        }
        return order;
    }

    public static List<Role> parseRoles(String rolesString) {
        List<Role> roles = new ArrayList<>();
        String[] roleDetails = rolesString.split(",\\s*");

        for (String roleDetail : roleDetails) {
            roles.add(Role.valueOf(roleDetail));
        }
        return roles;
    }
}
