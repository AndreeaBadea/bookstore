package com.softserve.bookstore.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Order {


    private int orderId;

    private LocalDateTime date;

    private Status status;

}
