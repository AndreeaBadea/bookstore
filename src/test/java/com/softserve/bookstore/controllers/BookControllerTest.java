package com.softserve.bookstore.controllers;

import com.softserve.bookstore.exceptions.BookNotFoundException;
import com.softserve.bookstore.generated.Author;
import com.softserve.bookstore.generated.Book;
import com.softserve.bookstore.generated.Genre;
import com.softserve.bookstore.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BookControllerTest extends BaseControllerTest<Book> {

    private static final String FILE_NAME = "src/main/resources/books";

    @MockBean
    private BookService bookService;

    @Test
    void getAllBooks_Returns_OK() throws Exception {
        Book book = new Book(1, "STrula", new Author(1, " Bula ", "Mih223ai"),
                Genre.FICTION, 12000);

        List<Book> books = new ArrayList<>();
        books.add(book);

        when(bookService.findAllBooks()).thenReturn(books);

        ResultActions resultActions = mockMvc.perform(get("/books"));

        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idBook", is(book.getIdBook())));

        assertThat(book.getIdBook()).isPositive();
        assertThat(book.getIdBook()).isEqualTo(1);
        assertNotNull(books);

    }

    @Test
    void getAllBooksFromFile_Returns_OK() throws Exception {
        List<Book> booksListFromFile = List.of(new Book(1, "White Fang", new Author("George", "Martin"), Genre.HORROR, 1200));
        when(bookService.getBooksFromFile(FILE_NAME)).thenReturn(booksListFromFile);

        ResultActions resultActions = mockMvc.perform(get("/books/file"));
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idBook").isNotEmpty())
                .andExpect(jsonPath("$[0].title").value("White Fang"));

        assertNotNull(booksListFromFile);
        assertThat(booksListFromFile).hasSize(1).isNotEmpty();

    }

    @Test
    void addBook_Return_CREATED_book() throws Exception {
        Book book = new Book(1, "STrula", new Author(1, " Bula ", "Mih223ai"),
                Genre.FICTION, 12000);

        ResultActions actions = mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON).content(jacksonTester.write(book).getJson()));
        actions.andExpect(status().isCreated()).andReturn();

        assertThat(book).isNotNull();

    }

    @Test
    void addBooksFromFile_Returns_CREATED() throws Exception {

        mvcResult = mockMvc.perform(post("/books/file").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();

        verify(bookService, times(1)).addBooksFromFile(FILE_NAME);
        String content = mvcResult.getResponse().getContentAsString();
        assertEquals("Added Books Successful", content);


    }


    @Test
    void deleteBook_Returns_OK() throws Exception {
        Book book = new Book(1, "STrula", new Author(1, " Bula ", "Mih223ai"),
                Genre.FICTION, 12000);

        when(bookService.deleteBooks(book.getIdBook())).thenReturn(true);
        mockMvc.perform(delete("/books/" + book.getIdBook())).andExpect(status().isOk());

        verify(bookService, times(1)).deleteBooks(book.getIdBook());

    }

    @Test
    void findBooksByGenre_Returns_OK() throws Exception{

        List<Book>books = Arrays.asList(
                new Book(1, "STrula", new Author(1, " Bula ", "Mih223ai"),
                        Genre.FICTION, 12000),
                 new Book(2, "STrula", new Author(2, " Diaconescu ", "Borcea"),
                Genre.BIOGRAPHY, 12000)
        );

        when(bookService.findBooksByGenre(Genre.FICTION)).thenReturn(books);

        mockMvc.perform(get("/books/search").param("genre","FICTION")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        verify(bookService,times(1)).findBooksByGenre(Genre.FICTION);

    }


    @Test
    void findBook_Returns_OK() throws Exception {
        Book book = new Book(1, "STrula", new Author(1, " Bula ", "Mih223ai"),
                Genre.FICTION, 12000);

        when(bookService.findBookById(book.getIdBook())).thenReturn(book);

        mockMvc.perform(get("/books/" + book.getIdBook())).andExpect(status().isOk());
        verify(bookService, times(1)).findBookById(book.getIdBook());

    }

    @Test
    void findBook_Throws_BookNotFoundException() throws BookNotFoundException {
        Book book = new Book(1, "STrula", new Author(1, " Bula ", "Mih223ai"),
                Genre.FICTION, 12000);

        when(bookService.findBookById(book.getIdBook())).thenThrow(new BookNotFoundException("Book could not be found"));

        try {
            bookService.findBookById(book.getIdBook());
            fail("Expected BookNotFound To Be Thrown");
        } catch (BookNotFoundException e) {
            System.out.println("Book has been found");
        }

        verify(bookService, times(1)).findBookById(book.getIdBook());
    }


    @Test
    void deleteBook_Returns_NOT_FOUND() throws Exception {

        mvcResult = mockMvc.perform(delete("/books/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();
        String msg = mvcResult.getResponse().getContentAsString();

        assertEquals("", msg);

    }


    @Test
    void addBooks_Fails_SQL_EXCEPTION() throws Exception {
        doThrow(new SQLException("**********************")).
                when(bookService).addBooksFromFile(FILE_NAME);
        mockMvc.perform(post("/books/file").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Exception exception = assertThrows(SQLException.class, () -> {
            bookService.addBooksFromFile(FILE_NAME);
        });
        assertEquals("**********************", exception.getMessage());
    }

    @Test
    void getAllBooksFromFile_Fails_IOEXCEPTION() throws Exception {
        doThrow(new IOException("**********")).
                when(bookService).getBooksFromFile(FILE_NAME);
        mockMvc.perform(get("/books/file").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Exception exception = assertThrows(IOException.class, () -> {
            bookService.getBooksFromFile(FILE_NAME);
        });
        assertEquals("**********", exception.getMessage());
    }
}
