package com.softserve.bookstore.soap;

import com.softserve.bookstore.generated.Book;
import com.softserve.bookstore.generated.BookDto;
import com.softserve.bookstore.generated.DeleteBookRequest;
import com.softserve.bookstore.generated.DeleteBookResponse;
import com.softserve.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

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
}
