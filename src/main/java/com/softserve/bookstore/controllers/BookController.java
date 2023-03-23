package com.softserve.bookstore.controllers;

import com.softserve.bookstore.models.Book;
import com.softserve.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    public static final String FILE_NAME = "src/main/resources/books";

    @Autowired
    BookService bookService;


    @GetMapping()
    public ResponseEntity<List<Book>> findBook() throws IOException {
        bookService.getBooksFromFile(FILE_NAME);

        return ResponseEntity.ok(bookService.getBooksFromFile(FILE_NAME));
    }
    @PostMapping()
    public ResponseEntity<String> addBook() throws SQLException, IOException {
        bookService.addBook(FILE_NAME);
        return new ResponseEntity<>("Added Books Succesfull",HttpStatus.CREATED);
    }



}
