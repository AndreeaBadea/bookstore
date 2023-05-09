package com.softserve.bookstore.service;

import com.softserve.bookstore.data.ManageBookData;
import com.softserve.bookstore.generated.Book;
import com.softserve.bookstore.repositories.BookRepository;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;


    @Autowired
    private ManageBookData manageBookData;

    public List<Book> getBooksFromFile(String fileName) throws IOException {
        return manageBookData.readData(fileName);
    }


    public List<Book> findAllBooks() throws SQLException {
        return bookRepository.findAll();


    }


    public void addBook(String fileName) throws IOException, SQLException {
        List<Book> books = getBooksFromFile(fileName);
        bookRepository.addBooks(books);

    }

    public boolean deleteBooks(int id) throws SQLException {

        return  bookRepository.removeBook(id);



    }
}