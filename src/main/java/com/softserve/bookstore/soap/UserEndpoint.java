package com.softserve.bookstore.soap;

import com.softserve.bookstore.exceptions.UserNotFoundException;
import com.softserve.bookstore.generated.GetUserRequest;
import com.softserve.bookstore.generated.GetUserResponse;
import com.softserve.bookstore.generated.UserDto;
import com.softserve.bookstore.models.User;
import com.softserve.bookstore.models.dtos.mappers.UserMapper;
import com.softserve.bookstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.xml.bind.JAXBException;
import java.sql.SQLException;

@Endpoint
public class UserEndpoint {

    private static final String NAMESPACE_URI = "http://www.softserve.com/bookstore/generated";

    @Autowired
    private UserService userService;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getUserRequest")
    @ResponsePayload
    public GetUserResponse getUserById(@RequestPayload GetUserRequest request) throws UserNotFoundException, SQLException, JAXBException {
        GetUserResponse response = new GetUserResponse();
        User user = userService.getUserById(request.getId());
        UserDto userDto = UserMapper.toUserDto(user);
        response.setUserDto(userDto);
        return response;
    }




}
