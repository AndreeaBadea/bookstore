package com.softserve.bookstore.models.dtos;

import com.softserve.bookstore.models.Book;
import com.softserve.bookstore.models.Role;
import com.softserve.bookstore.models.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;
import java.sql.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "OrderDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class OrderDto {

    private int orderId;

    private Date date;

    @XmlElementWrapper(name = "books")
    @XmlElement(name = "book")
    private List<Book> books;

    @XmlElement(name = "status")
    private Status status;


}
