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

    @Id
    private int userId;

    private String email;

    private String password;

    private List<Order> orders;

    private List<Role> roles;

    public User(int userId, String email, String password) {
        this.userId = userId;
        this.email = email;
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", orders=" + orders +
                ", roles=" + roles +
                '}';
    }
}