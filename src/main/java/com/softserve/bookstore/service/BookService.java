package com.softserve.bookstore.service;

import com.softserve.bookstore.data.ReadDataFromBookFile;
import com.softserve.bookstore.models.Book;
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
    private ReadDataFromBookFile readDataFromBookFile;

    public List<Book> getBooksFromFile(String fileName) throws IOException {
        return readDataFromBookFile.readData(fileName);
    }


    public void findAuthorById(String fileName) throws SQLException, IOException {
        List<Book> books = getBooksFromFile(fileName);
        bookRepository.findAll();

    }


    public void addBook(String fileName) throws IOException, SQLException {
        List<Book> books = getBooksFromFile(fileName);
        bookRepository.addBooks(books);

    }

    public boolean deleteBooks(int id) throws SQLException {

        return  bookRepository.removeBook(id);



    }
}