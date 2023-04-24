package com.softserve.bookstore.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.*;
import java.sql.Date;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "orderDto", propOrder = {
        "orderId",
        "date",
        "books",
        "status"
})
public class OrderDto {

    private int orderId;

    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    private Date date;

    private List<Book> books;

    @XmlElement(required = true)
    private Status status;

    public OrderDto(int orderId, Date date, Status status) {
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
