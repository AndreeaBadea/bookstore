package com.softserve.bookstore.controller;

import com.softserve.bookstore.models.User;
import com.softserve.bookstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@AutoConfigureJsonTesters
public class BaseControllerTest<T> {

    @MockBean
    protected UserService userService;

    @Autowired
    protected MockMvc mockMvc;

    protected MvcResult mvcResult;

    @Autowired
    protected JacksonTester<T> jacksonTester;
}
