package com.softserve.bookstore.models.dtos;

import com.softserve.bookstore.models.Order;
import com.softserve.bookstore.models.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "UserDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserDto {

    private int userId;

    private String email;

    @XmlElementWrapper(name = "roles")
    @XmlElement(name = "role")
    private List<Role> roles;

    @XmlElementWrapper(name = "orders")
    @XmlElement(name = "order")
    private List<Order> orders;

}
