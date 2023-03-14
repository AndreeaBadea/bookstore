package com.softserve.bookstore.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class User {


    @Id
    private int id;

    private String email;

    private String password;

    private Order orderId;

//    private Role role;
}