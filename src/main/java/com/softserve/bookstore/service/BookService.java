package com.softserve.bookstore.service;

import com.softserve.bookstore.data.ManageBookData;
import com.softserve.bookstore.exceptions.BookNotFoundException;
import com.softserve.bookstore.generated.Book;
import com.softserve.bookstore.generated.Genre;
import com.softserve.bookstore.models.Newsletter;
import com.softserve.bookstore.repositories.BookRepository;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tinylog.Logger;


@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ManageBookData manageBookData;

    @Autowired
    private Newsletter newsletter;

    public List<Book> getBooksFromFile(String fileName) throws IOException {
        return manageBookData.readData(fileName);
    }


    public List<Book> findAllBooks() throws SQLException {
        return bookRepository.findAll();
    }

    public Book findBookById(int id) throws SQLException, BookNotFoundException {
        return bookRepository.getBookById(id);
    }

    public List<Book> findBooksByGenre(Genre genre) throws SQLException {
        return bookRepository.findBooksByGenre(genre);
    }

    public void addBooksFromFile(String fileName) throws IOException, SQLException {
        List<Book> books = getBooksFromFile(fileName);
        bookRepository.addBooks(books);

    }

    public Book addBook(Book book) throws SQLException {
        Book bookAdded = bookRepository.addBook(book);
        Logger.info("Book id {} successfully added to the database.", bookAdded.getIdBook());
        newsletter.notifyObservers("NEWSLETTER: Check out our new book, " + book.getTitle());
        return bookAdded;
    }

    public boolean deleteBooks(int id) throws SQLException {
        return  bookRepository.removeBook(id);

    }

}