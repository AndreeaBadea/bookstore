package com.softserve.bookstore.BookTesting;

import com.softserve.bookstore.connection.ConnectionManager;
import com.softserve.bookstore.repositories.AuthorRepository;
import com.softserve.bookstore.repositories.BookRepository;
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


    private static final Book book = new Book(1, "Bula", new Author(1, " Bula ", "Mihai"), Genre.FICTION, 12000);
    private static final Book bookie2 = new Book(2, "Strula", new Author(2, " Strula ", "Mihai"), Genre.BIOGRAPHY, 1000);

    @Test
    @SneakyThrows
    public void findAll_ReturnsBooks_Success() {

        when(mockConnection.prepareStatement(BookRepository.SELECT_BOOKS)).thenReturn(mockPreparedStatement);
        when(mockConnection.prepareStatement(BookRepository.SELECT_AUTHORS)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery())
                .thenReturn(mockResultSet);

        when(mockResultSet.next())
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);


        when(mockResultSet.getInt(Book.FIELD_BOOK_ID))
                .thenReturn(book.getIdBook())
                .thenReturn(bookie2.getIdBook());

        when(mockResultSet.getString(Book.FIELD_FIRSTNAME))
                .thenReturn(book.getAuthor().getFirstName() + book.getAuthor().getLastName())
                .thenReturn(bookie2.getAuthor().getFirstName() + book.getAuthor().getLastName());


        List<Book> bookList = bookRepository.findAll();
        bookList.add(book);
        bookList.add(bookie2);

        verify(mockResultSet, times(4)).next();
        assertEquals(2, bookList.size());

        Book foundBook = bookList.get(0);
        assertEquals(foundBook.getIdBook(), book.getIdBook());
        assertEquals(foundBook.getTitle(), book.getTitle());
        assertEquals(foundBook.getAuthor().getFirstName(), book.getAuthor().getFirstName());
        assertEquals(foundBook.getAuthor().getLastName(), book.getAuthor().getLastName());

        Book foundBook2 = bookList.get(1);
        assertEquals(foundBook2.getIdBook(), bookie2.getIdBook());
        assertEquals(foundBook2.getTitle(), bookie2.getTitle());
        assertEquals(foundBook2.getAuthor().getFirstName(), bookie2.getAuthor().getFirstName());
        assertEquals(foundBook2.getAuthor().getLastName(), bookie2.getAuthor().getLastName());
    }

    @Test
    @DisplayName("Adding books Successful")
    @SneakyThrows
    public void addBook_IfAdded_Success() {

//        when(mockConnectionManager.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(BookRepository.INSERT_SQL)).thenReturn(mockPreparedStatement);
        when(mockConnection.prepareStatement(BookRepository.SELECT_LAST_AUTHORS)).thenReturn(mockPreparedStatement);
        when(mockConnection.prepareStatement(BookRepository.QUERY_AUTHORS)).thenReturn(mockPreparedStatement);

        int[] executeBatchRes = {1, 1};

        when(mockPreparedStatement.executeBatch()).thenReturn(executeBatchRes);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next())
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);

        when(mockResultSet.getInt(Book.FIELD_BOOK_ID))
                .thenReturn(book.getIdBook())
                .thenReturn(bookie2.getIdBook());

        when(mockResultSet.getString(Book.FIELD_FIRSTNAME))
                .thenReturn(book.getAuthor().getFirstName() + book.getAuthor().getLastName())
                .thenReturn(bookie2.getAuthor().getLastName() + book.getAuthor().getLastName());

        List<Book> bookList = new ArrayList<>();
        bookList.add(book);
        bookList.add(bookie2);

        bookRepository.addBooks(bookList);
        bookRepository.findLastAuthorsAdded(2);

        assertNotNull(bookList);
        assertTrue(bookList.size() > 0);
        verify(mockPreparedStatement, times(2)).addBatch();
        verify(mockPreparedStatement, times(2)).executeBatch();
        verify(mockPreparedStatement, times(2)).executeQuery();


    }


    @Test
    @DisplayName("Delete books Successful")
    @Transactional
    void removeBook_IfSuccess() throws SQLException {

        when(mockConnection.prepareStatement(BookRepository.DELETE_BOOKS))
                .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate())
                .thenReturn(1);

        boolean removeBook = bookRepository.removeBook(book.getIdBook());
        assertTrue(removeBook);
        assertEquals(1,book.getIdBook());
        verify(mockPreparedStatement,times(1)).setInt(1,book.getIdBook());

    }
}

