package com.softserve.bookstore.models.dtos;


import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetUserResponse", propOrder = "userDto")
@XmlRootElement
public class GetUserResponse {

    @XmlElement(required = true)
    protected UserDto userDto;

    public UserDto getUser() {
        return userDto;
    }

    public void setUser(UserDto user) {
        this.userDto = user;
    }
}
