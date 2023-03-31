package com.softserve.bookstore.service;

import com.softserve.bookstore.models.Book;
import com.softserve.bookstore.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Service
public class BookService {


    @Autowired
    BookRepository bookRepository;


    public List<Book>getBooksFromFile(String fileName)throws IOException {
        return bookRepository.findBooks(fileName);
    }


    public void findAuthorById(String fileName) throws SQLException, IOException {
        List<Book>books =getBooksFromFile(fileName);
        bookRepository.findAll();
    }





    public void addBook(String fileName) throws IOException, SQLException {
        List<Book>books = getBooksFromFile(fileName);
        bookRepository.addBook(books);

    }
}
