package com.softserve.bookstore.controllers;

import com.softserve.bookstore.models.Author;
import com.softserve.bookstore.models.Book;
import com.softserve.bookstore.models.Genre;
import com.softserve.bookstore.repositories.BookRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

    MvcResult mvcResult;
    @Autowired
    public JacksonTester<T> jacksonTester;
}
