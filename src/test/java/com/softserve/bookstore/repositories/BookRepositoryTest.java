package com.softserve.bookstore.repositories;

import com.softserve.bookstore.connection.ConnectionManager;
import com.softserve.bookstore.generated.Author;
import com.softserve.bookstore.generated.Book;
import com.softserve.bookstore.generated.Genre;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class BookRepositoryTest {

    @InjectMocks
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;
    @Mock
    private ConnectionManager mockConnectionManager;

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;


    private static final Book firstBook = new Book(1, "GoF", new Author(1, " Bula ", "Mihai"), Genre.FICTION, 12000);
    private static final Book secondBook = new Book(2, "Clean Code", new Author(2, " Strula ", "Mihai"), Genre.BIOGRAPHY, 1000);

    @Test
    @SneakyThrows
    public void findAll_ReturnsTwoBooks_Success() {
        when(mockConnection.prepareStatement(BookRepository.SELECT_BOOKS)).thenReturn(mockPreparedStatement);
        when(mockConnection.prepareStatement(BookRepository.SELECT_AUTHORS)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery())
                .thenReturn(mockResultSet);

        when(mockResultSet.next())
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);


        when(mockResultSet.getInt(Book.FIELD_BOOK_ID))
                .thenReturn(firstBook.getIdBook())
                .thenReturn(secondBook.getIdBook());

        when(mockResultSet.getString(Book.FIELD_FIRSTNAME))
                .thenReturn(firstBook.getAuthor().getFirstName() + firstBook.getAuthor().getLastName())
                .thenReturn(secondBook.getAuthor().getFirstName() + firstBook.getAuthor().getLastName());


        List<Book> bookList = bookRepository.findAll();
        bookList.add(firstBook);
        bookList.add(secondBook);

        verify(mockResultSet, times(4)).next();
        assertEquals(2, bookList.size());

        Book foundBook = bookList.get(0);
        assertEquals(foundBook.getIdBook(), firstBook.getIdBook());
        assertEquals(foundBook.getTitle(), firstBook.getTitle());
        assertEquals(foundBook.getAuthor().getFirstName(), firstBook.getAuthor().getFirstName());
        assertEquals(foundBook.getAuthor().getLastName(), firstBook.getAuthor().getLastName());

        Book foundBook2 = bookList.get(1);
        assertEquals(foundBook2.getIdBook(), secondBook.getIdBook());
        assertEquals(foundBook2.getTitle(), secondBook.getTitle());
        assertEquals(foundBook2.getAuthor().getFirstName(), secondBook.getAuthor().getFirstName());
        assertEquals(foundBook2.getAuthor().getLastName(), secondBook.getAuthor().getLastName());
    }

    @Test
    @DisplayName("Adding books Successful")
    @SneakyThrows
    public void addBook_ReturnsBook_Success() {
        when(mockConnection.prepareStatement(BookRepository.INSERT_BOOK)).thenReturn(mockPreparedStatement);
        when(mockConnection.prepareStatement(BookRepository.SELECT_LAST_AUTHORS)).thenReturn(mockPreparedStatement);
        when(mockConnection.prepareStatement(BookRepository.INSERT_AUTHOR)).thenReturn(mockPreparedStatement);

        int[] executeBatchRes = {1, 1};

        when(mockPreparedStatement.executeBatch()).thenReturn(executeBatchRes);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next())
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);

        when(mockResultSet.getInt(Book.FIELD_BOOK_ID))
                .thenReturn(firstBook.getIdBook())
                .thenReturn(secondBook.getIdBook());

        when(mockResultSet.getString(Book.FIELD_FIRSTNAME))
                .thenReturn(firstBook.getAuthor().getFirstName() + firstBook.getAuthor().getLastName())
                .thenReturn(secondBook.getAuthor().getLastName() + firstBook.getAuthor().getLastName());

        List<Book> bookList = new ArrayList<>();
        bookList.add(firstBook);
        bookList.add(secondBook);

        bookRepository.addBooks(bookList);
        bookRepository.findLastAuthorsAdded(2);

        assertNotNull(bookList);
        assertTrue(bookList.size() > 0);
        verify(mockPreparedStatement, times(2)).addBatch();
        verify(mockPreparedStatement, times(2)).executeBatch();
        verify(mockPreparedStatement, times(2)).executeQuery();


    }


    @Test
    @DisplayName("Delete book success")
    @Transactional
    void deleteBook_Returns_True() throws SQLException {
        when(mockConnection.prepareStatement(BookRepository.DELETE_BOOKS))
                .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate())
                .thenReturn(1);

        boolean removeBook = bookRepository.removeBook(firstBook.getIdBook());
        assertTrue(removeBook);
        assertEquals(1, firstBook.getIdBook());
        verify(mockPreparedStatement,times(1)).setInt(1, firstBook.getIdBook());

    }





}

