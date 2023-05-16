package com.softserve.bookstore.soap;


import com.softserve.bookstore.exceptions.BookNotFoundException;
import com.softserve.bookstore.generated.*;
import com.softserve.bookstore.models.dtos.mappers.BookMapper;
import com.softserve.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Endpoint
public class BookEndpoint {

    private static final String NAMESPACE_URI = "http://www.softserve.com/bookstore/generated";

    @Autowired
    private BookService bookService;



    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteBookRequest")
    @ResponsePayload
    public DeleteBookResponse deleteBook(@RequestPayload DeleteBookRequest request) throws Exception {

        if (bookService.deleteBooks(request.getId())) {

        } else {
            throw new Exception(new Exception("Could not find the ID"));
        }
        DeleteBookResponse response = new DeleteBookResponse();
        response.setMessage("Deleted succesfully");
        return response;

    }



    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getBookRequest")
    @ResponsePayload
    public GetBookResponse getBookByID(@RequestPayload GetBookRequest request) throws SQLException, BookNotFoundException {

        int bookId = request.getId();

        Book book = bookService.findBookById(bookId);

        if (book == null) {
            throw new BookNotFoundException("The book with id " + bookId + " do not exist");
        }

        BookDto bookDto = BookMapper.toBookDto(book);

        GetBookResponse response = new GetBookResponse();
        response.setUserDto(bookDto);
        return response;
    }


    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAllBooksRequest")
    @ResponsePayload
    public GetAllBooksResponse getAllBooks() throws SQLException {

        GetAllBooksResponse response = new GetAllBooksResponse();

        List<Book> books = bookService.findAllBooks();
        List<BookDto> bookDto = books.stream().map(BookMapper::toBookDto)
                .collect(Collectors.toList());
        bookDto.forEach(System.out::println);
        response.getBookDto();
        return response;


    }

    @PayloadRoot(namespace = NAMESPACE_URI , localPart = "addBooksRequest")
    @ResponsePayload
    public AddBooksResponse addBooks (@RequestPayload AddBooksRequest request) throws SQLException, IOException {

        AddBooksResponse response = new AddBooksResponse();

        Book bookRequest = new Book(
                request.getBook().getIdBook(),
                request.getBook().getTitle(),
                request.getBook().getAuthor(),
                request.getBook().getGenre(),
                request.getBook().getPrice());

        Book addBook = bookService.addBook(bookRequest);
        response.setBookDto(BookMapper.toBookDto(addBook));
        return response;

    }
}
