package com.softserve.bookstore.soap;

import com.softserve.bookstore.exceptions.UserNotFoundException;
import com.softserve.bookstore.models.User;
import com.softserve.bookstore.models.dtos.GetUserRequest;
import com.softserve.bookstore.models.dtos.GetUserResponse;
import com.softserve.bookstore.models.dtos.UserDto;
import com.softserve.bookstore.models.dtos.mappers.UserMapper;
import com.softserve.bookstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.sql.SQLException;

@Endpoint
public class UserEndpoint {

    private static final String NAMESPACE_URI = "http://localhost:8080/users";

    @Autowired
    private UserService userService;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getUserRequest")
    @ResponsePayload
    public GetUserResponse getUserById(@RequestPayload GetUserRequest request) throws UserNotFoundException, SQLException {
        GetUserResponse response = new GetUserResponse();
        User user = userService.getUserById(request.getUserId());
        UserDto userDto = UserMapper.toUserDto(user);
        response.setUser(userDto);
        return response;
    }
}
