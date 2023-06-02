package com.softserve.bookstore.controllers;

//import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureJsonTesters
@AutoConfigureMockMvc
public class BaseControllerTest<T> {

    @Autowired
    public MockMvc mockMvc;

    protected MvcResult mvcResult;

    @Autowired
    public JacksonTester<T> jacksonTester;

}
