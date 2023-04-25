package com.softserve.bookstore.models;

import com.softserve.bookstore.models.OrderDto;
import com.softserve.bookstore.models.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "user", propOrder = {
        "userId",
        "email",
        "password",
        "orders",
        "roles"
})
public class User {

    public static final String FIELD_USER_ID = "id_user";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_PASSWORD = "password";
    public static final String FIELD_ORDERS = "orders";
    public static final String FIELD_ROLES = "roles";

    @Id
    private int userId;
    
    @XmlElement(required = true)
    private String email;

    @XmlElement(required = true)
    private String password;

    private List<OrderDto> orders;

    private List<Role> roles;

    public User(int userId, String email, String password) {
        this.userId = userId;
        this.email = email;
        this.password = password;
    }

    public User(String email, String password,  List<Role> roles) {
        this.email = email;
        this.password = password;
        this.roles = roles;
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