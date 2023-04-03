package com.softserve.bookstore.controllers;

import com.softserve.bookstore.exceptions.BookNotFoundException;
import com.softserve.bookstore.models.Book;
import com.softserve.bookstore.models.ErrorResponse;
import com.softserve.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tinylog.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    public static final String FILE_NAME = "src/main/resources/books";

    @Autowired
    private BookService bookService;


    @GetMapping()
    public ResponseEntity<List<Book>> findBook() throws IOException {
        bookService.getBooksFromFile(FILE_NAME);

        return ResponseEntity.ok(bookService.getBooksFromFile(FILE_NAME));
    }

    @PostMapping()
    public ResponseEntity<String> addBook() throws SQLException, IOException {
        bookService.addBook(FILE_NAME);
        return new ResponseEntity<>("Added Books Successful", HttpStatus.CREATED);
    }


    @GetMapping("/author")
    public ResponseEntity<String> findAuthorId() throws SQLException, IOException {
        bookService.findAuthorById(FILE_NAME);
        return new ResponseEntity<>("Authors found", HttpStatus.OK);
    }

    @ExceptionHandler({BookNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleBookEx(BookNotFoundException msg) {
        Logger.error(" Failed to find the Book ", msg);
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND, msg.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({SQLException.class})
    public ResponseEntity<ErrorResponse> handleSQlEx(SQLException msg) {
        Logger.error(" Failed to execute query ", msg);
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST, msg.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({SQLException.class})
    public ResponseEntity<ErrorResponse> handleIoEx(SQLException msg) {
        Logger.error(" Failed to process the file ", msg);
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST, msg.getMessage()), HttpStatus.BAD_REQUEST);
    }


}
