package com.softserve.bookstore.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Order {

    private int orderId;

    private Date date;

    private List<Book> books;

    private Status status;

    public Order(int orderId, Date date, Status status) {
        this.orderId = orderId;
        this.date = date;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", date=" + date +
                ", status=" + status +
                '}';
    }
}
