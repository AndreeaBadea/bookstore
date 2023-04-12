package com.softserve.bookstore.controllers;

import com.softserve.bookstore.models.Author;
import com.softserve.bookstore.models.Book;
import com.softserve.bookstore.models.Genre;
import com.softserve.bookstore.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BookControllerTest extends BaseControllerTest<Book> {

    private static final String FILE_NAME = "src/main/resources/books";
    @MockBean
    BookService bookService;

    @Test
    @DisplayName("Finding book")
    void findBook_IF_Success() throws Exception {
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
    void getAlBookFromTxT_If_Success() throws Exception {


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
    @DisplayName("Adding books successful")
    void addBook_If_Success() throws Exception {

        Book book = new Book(1, "STrula", new Author(1, " Bula ", "Mih223ai"),
                Genre.FICTION, 12000);

        ResultActions actions = mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON).content(jacksonTester.write(book).getJson()));
        actions.andExpect(status().isCreated()).andReturn();

        assertThat(book).isNotNull();

    }

    @Test
    @DisplayName("Add book from file")
    void addBook_FromFile_If_Success() throws Exception {

        mvcResult = mockMvc.perform(post("/books").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();
        verify(bookService, times(1)).addBook(FILE_NAME);

        String content = mvcResult.getResponse().getContentAsString();
        assertEquals(content, "Added Books Successful");


    }


    @Test
    @DisplayName("Deleting books is successful")
    void deleteBook_If_Success() throws Exception {

        Book book = new Book(1, "STrula", new Author(1, " Bula ", "Mih223ai"),
                Genre.FICTION, 12000);

        when(bookService.deleteBooks(book.getIdBook())).thenReturn(true);

        ResultActions actions = mockMvc.perform(delete("/books/" + book.getIdBook()))
                .andExpect(status().isOk());

        assertThat(actions.equals(delete("/books/"))).isNotNull();

    }


    @Test
    @DisplayName("Deleting books is not possible")
    void deleteBook_If_NOT_FOUND() throws Exception {

        mvcResult = mockMvc.perform(delete("/books/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();
        String msg = mvcResult.getResponse().getContentAsString();

        assertEquals("", msg);

    }


    @Test
    void handleSQlEx_If_Book_Fail() throws Exception {

        doThrow(new SQLException("**********************")).
                when(bookService).addBook(FILE_NAME);
        mockMvc.perform(post("/books").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Exception exception = assertThrows(SQLException.class, () -> {
            bookService.addBook(FILE_NAME);
        });
        assertEquals("**********************", exception.getMessage());
    }

    @Test
    void handleIoEx_IF_GetBook_Fail() throws Exception {

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
