package com.softserve.bookstore.models.dtos.mappers;

import com.softserve.bookstore.generated.UserDto;
import com.softserve.bookstore.models.User;

public class UserMapper {

    public static UserDto toUserDto(User user){
        UserDto userDto = new UserDto();
        userDto.setUserId(user.getUserId());
        userDto.setEmail(user.getEmail());
        userDto.setRoles(user.getRoles());
        userDto.setOrders(user.getOrders());
        return userDto;
    }
}
