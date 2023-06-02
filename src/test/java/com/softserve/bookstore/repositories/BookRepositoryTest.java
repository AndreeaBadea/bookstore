package com.softserve.bookstore.repositories;

import com.softserve.bookstore.connection.ConnectionManager;
import com.softserve.bookstore.exceptions.BookNotFoundException;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


import static com.softserve.bookstore.repositories.BookRepository.SELECT_BOOKS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class BookRepositoryTest extends BookUtil {

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
    void findAll_ReturnsTwoBooks_Success() {
        when(mockConnection.prepareStatement(SELECT_BOOKS)).thenReturn(mockPreparedStatement);
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
     void addBook_ReturnsBook_Success() {
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
        verify(mockPreparedStatement, times(1)).setInt(1, firstBook.getIdBook());

    }


    @Test
    void getBookFromResultSet_AddsBookstoList() throws SQLException {

        List<Book> books = new ArrayList<>();
        List<Author> authors = new ArrayList<>();
        Author author1 = new Author(1, "George", "Martin");
        Author author2 = new Author(2, "Mike", "Stark");
        authors.add(author1);
        authors.add(author2);


        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("id")).thenReturn(1, 2);
        when(mockResultSet.getString("title")).thenReturn("White Fang", "Dark Lotus");
        when(mockResultSet.getInt("id_author")).thenReturn(1, 2);
        when(mockResultSet.getString("genre")).thenReturn("HORROR", "ROMANCE");
        when(mockResultSet.getFloat("price")).thenReturn(1200.0f, 500.0f);


        BookUtil.getBooksFromResultSet(mockResultSet, books, authors);


        assertEquals(2, books.size());
        assertThat(books.contains("White Fang"));

    }

    @Test
    void getBookById_ReturnsBookWhenFound() throws SQLException, BookNotFoundException {

        when(mockConnection.prepareStatement(FIND_BOOK_ID)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next())
                .thenReturn(true);

        int expectedId = 1;
        String expectedTitle = "White Fang";

        when(mockResultSet.getInt("id")).thenReturn(expectedId);
        when(mockResultSet.getString("title")).thenReturn(expectedTitle);

        Book result = bookRepository.getBookById(expectedId);

        assertNotNull(result);
        assertEquals(expectedId, result.getIdBook());
        assertEquals(expectedTitle, result.getTitle());

    }

    @Test
    void getAuthorByName_RETURNS_OK() throws SQLException {

        when(mockConnection.prepareStatement(QUERY)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next())
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);
        when(mockResultSet.getInt("id_author")).thenReturn(1, 2);

        List<Author> result = bookRepository.findAuthorByName(firstBook.getAuthor().getFirstName(),
                firstBook.getAuthor().getLastName());
        assertNotNull(result);
        assertEquals(2, result.size());

        verify(mockPreparedStatement).setString(1, firstBook.getAuthor().getFirstName());
        verify(mockPreparedStatement).setString(2, firstBook.getAuthor().getLastName());
        verify(mockResultSet, times(3)).next();
        verify(mockResultSet, times(2)).getInt("id_author");

    }

    @Test
    void addAuthors_RETURNS_OK() throws SQLException {

        when(mockConnection.prepareStatement(INSERT_AUTHOR)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next())
                .thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1);

        Author savedAuthor = bookRepository.addAuthor(firstBook.getAuthor());
        verify(mockConnection).prepareStatement(INSERT_AUTHOR);
        verify(mockPreparedStatement).setString(1," Bula ");
        verify(mockPreparedStatement).setString(2,"Mihai");
        verify(mockPreparedStatement).executeUpdate();
        verify(mockPreparedStatement).getGeneratedKeys();
        verify(mockResultSet).next();
        verify(mockResultSet).getInt(1);

        assertNotNull(savedAuthor);
        assertEquals(1,savedAuthor.getIdAuthor());
        assertEquals(" Bula ",savedAuthor.getFirstName());
        assertEquals("Mihai",savedAuthor.getLastName());

    }
}

