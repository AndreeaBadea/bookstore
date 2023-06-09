package com.softserve.bookstore.controllers;
import com.softserve.bookstore.exceptions.BookNotFoundException;
import com.softserve.bookstore.generated.Book;
import com.softserve.bookstore.generated.BookDto;
import com.softserve.bookstore.generated.Genre;
import com.softserve.bookstore.models.DiscountParameters;
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


    @GetMapping("/file")
    public ResponseEntity<List<Book>> getAllBooksFromFile() throws IOException {
        bookService.getBooksFromFile(FILE_NAME);
        return ResponseEntity.ok(bookService.getBooksFromFile(FILE_NAME));
    }

    @PostMapping("/file")
    public ResponseEntity<String> addBooksFromFile() throws SQLException, IOException {
        bookService.addBooksFromFile(FILE_NAME);
        return new ResponseEntity<>("Added Books Successful", HttpStatus.CREATED);
    }

    @PostMapping()
    public ResponseEntity<Book> addBook(@RequestBody Book book) throws SQLException {
       return new ResponseEntity<>(bookService.addBook(book), HttpStatus.CREATED);
    }


    @GetMapping()
    public ResponseEntity<List<Book>> findBooks() throws SQLException {
        List<Book> books = bookService.findAllBooks();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> findBook(@PathVariable int id) throws SQLException, BookNotFoundException {
        bookService.findBookById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Book>> findBooksByGenre(@RequestParam String genre) throws SQLException {
        return new ResponseEntity<>(bookService.findBooksByGenre(Genre.valueOf(genre)), HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable int id) throws SQLException {
        if (bookService.deleteBooks(id)){
            return ResponseEntity.ok("Book deleted successful");
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @ExceptionHandler({SQLException.class})
    public ResponseEntity<ErrorResponse> handleSQlEx(SQLException msg) {
        Logger.error(" Failed to execute query ", msg);
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST, msg.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({IOException.class})
    public ResponseEntity<ErrorResponse> handleIoEx(IOException msg) {
        Logger.error(" Failed to process the file ", msg);
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST, msg.getMessage()), HttpStatus.BAD_REQUEST);
    }


}
