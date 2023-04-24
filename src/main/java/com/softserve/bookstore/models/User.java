package com.softserve.bookstore.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class User {

    public static final String FIELD_USER_ID = "id_user";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_PASSWORD = "password";
    public static final String FIELD_ORDERS = "orders";
    public static final String FIELD_ROLES = "roles";

    @Id
    private int userId;

    private String email;

    private String password;

    private List<OrderDto> orders;

    private List<Role> roles;

    public User(int userId, String email, String password) {
        this.userId = userId;
        this.email = email;
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" + "userId=" + userId
                + ", email='" + email
                + '\'' + ", password='" + password + '\''
                + ", orders=" + orders
                + ", roles=" + roles
                + '}';
    }
}