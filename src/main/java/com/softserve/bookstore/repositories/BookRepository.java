package com.softserve.bookstore.repositories;


import com.softserve.bookstore.connection.ConnectionManager;
import com.softserve.bookstore.exceptions.BookNotFoundException;
import com.softserve.bookstore.exceptions.CustomExceptionAuthor;
import com.softserve.bookstore.generated.Author;
import com.softserve.bookstore.generated.Book;
import com.softserve.bookstore.generated.Genre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.tinylog.Logger;

import javax.annotation.PostConstruct;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.softserve.bookstore.repositories.AuthorRepository.AuthorUtil.addToBatch;


@Repository
public class BookRepository {

    public static final String INSERT_BOOK = "INSERT INTO books (title,id_author,genre,price) VALUES (?,?,?,?)";
    public static final String QUERY = "SELECT * FROM author WHERE first_name = ? AND last_name = ? ";
    public static final String SELECT_LAST_AUTHORS = "SELECT TOP 3 * FROM author ORDER BY id_author DESC";
    public static final String INSERT_AUTHOR = "INSERT INTO author(first_name,last_name) VALUES(?,?)";
    public static final String SELECT_BOOKS = "SELECT * FROM books";
    public static final String SELECT_AUTHORS = "SELECT * FROM author";
    public static final String DELETE_BOOKS = "DELETE FROM books WHERE id = ?";
    public static final String FIND_BOOK_ID = "SELECT * FROM books WHERE id = ?";
    public static final String SELECT_BOOKS_BY_GENRE = "SELECT * FROM books WHERE genre = ?";

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private ConnectionManager connectionManager;

    private Connection connection;

    @PostConstruct
    public void init() throws SQLException {
        connection = connectionManager.getConnection();
    }


    public static Optional<Author> getAuthorById(List<Author> authors, int id) {
        return authors.stream().filter(author -> id == (author.getIdAuthor()))
                .findFirst();
    }

    public List<Book> findBooksByGenre(Genre genre) throws SQLException {
        return findAll().stream()
                .filter(book -> book.getGenre().equals(genre))
                .collect(Collectors.toList());
    }


    public List<Book> findAll() throws SQLException {
        PreparedStatement bookStatement = connection.prepareStatement(SELECT_BOOKS);
        ResultSet resultSet = bookStatement.executeQuery();
        Logger.info("Books were successfully retrived from the database.");
        return getBookList(resultSet);
    }

    public List<Author> findAuthorByName(String firstName, String lastName) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(QUERY);
        preparedStatement.setString(1, firstName);
        preparedStatement.setString(2, lastName);
        ResultSet rs = preparedStatement.executeQuery();

        List<Author> result = new ArrayList<>();
        while (rs.next()) {
            Author item = new Author();
            item.setIdAuthor(rs.getInt("id_author"));
            result.add(item);
        }
        return result;
    }

    public List<Author> findLastAuthorsAdded(int numberOfRecords) throws SQLException {
        PreparedStatement authorStatement = connection.prepareStatement(SELECT_LAST_AUTHORS);
        ResultSet resultSet = authorStatement.executeQuery();

        List<Author> authors = new ArrayList<>();
        return AuthorRepository.AuthorUtil.getAuthors(resultSet, authors);
    }


    public void addAuthors(List<Author> authors) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(INSERT_AUTHOR);
        authors.forEach(author -> addToBatch(statement, author));
        statement.executeBatch();
    }

    public List<Book> getBookList(ResultSet resultSet) throws SQLException {
        PreparedStatement authorStatement = connection.prepareStatement(SELECT_AUTHORS);
        ResultSet aut = authorStatement.executeQuery();

        List<Author> authors = AuthorRepository.AuthorUtil.getAuthorFromResultsSet(aut);
        List<Book> books = new ArrayList<>();

        BookUtil.getBooksFromResultSet(resultSet, books, authors);
        return books;
    }
//TODO parse the method addbooklist , parsing method for result set

    @Transactional
    public void addBooks(List<Book> bookList) throws SQLException {
        PreparedStatement bookStatement = connection.prepareStatement(INSERT_BOOK);
        List<Author> authorsListFromFile = new ArrayList<>();
        AuthorRepository.addingAuthorsFromList(bookList, authorsListFromFile);

        addAuthors(authorsListFromFile);
        List<Author> lastAuthorsAdded = findLastAuthorsAdded(3);
        authorRepository.addAuthorIdsToBooks(bookList, bookStatement, lastAuthorsAdded);
        bookStatement.executeBatch();
    }

    @Transactional
    public Author addAuthor(Author author) throws SQLException {
        PreparedStatement authorStatement = connection.prepareStatement(INSERT_AUTHOR);
        authorStatement.setString(1, author.getFirstName());
        authorStatement.setString(2, author.getLastName());
        authorStatement.executeUpdate();

        ResultSet generatedKeys = authorStatement.getGeneratedKeys();

        if (generatedKeys.next()) {
            int id = generatedKeys.getInt(1);
            author.setIdAuthor(id);
        }
        return author;
    }


    @Transactional
    public Book addBook(Book book) throws SQLException {
        PreparedStatement bookStatement = connection.prepareStatement(INSERT_BOOK, Statement.RETURN_GENERATED_KEYS);
        bookStatement.setString(1, book.getTitle());
        bookStatement.setInt(2, book.getAuthor().getIdAuthor());
        bookStatement.setString(3, book.getGenre().name());
        bookStatement.setFloat(4, book.getPrice());
        bookStatement.executeUpdate();

        ResultSet generatedKeys = bookStatement.getGeneratedKeys();
        if (generatedKeys.next()) {
            int id = (int) generatedKeys.getLong(1);
            book.setIdBook(id);
        }
        return book;
    }


    @Transactional
    public boolean removeBook(int id) throws SQLException {
        PreparedStatement bookStatement = connection.prepareStatement(DELETE_BOOKS);
        bookStatement.setInt(1, id);
        return bookStatement.executeUpdate() > 0;

    }

    public Book getBookById(int id) throws BookNotFoundException {
        try (PreparedStatement statement = connection.prepareStatement(FIND_BOOK_ID)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Book book = new Book();
                    book.setIdBook(resultSet.getInt("id"));
                    book.setTitle(resultSet.getString("title"));
                    return book;
                } else {
                    return null;
                }
            }

        } catch (SQLException exception) {
            throw new BookNotFoundException("Failed to find the book with ID " + id);
        }
    }
}

class BookUtil extends BookRepository {


    public static void getBooksFromResultSet(ResultSet resultSet, List<Book> books, List<Author> authors) throws SQLException {

        while (resultSet.next()) {
            int bookId = resultSet.getInt("id_book");
            String title = resultSet.getString("title");
            int idAuthor = resultSet.getInt("id_author");
            Author author = getAuthorById(authors, idAuthor).orElseThrow(() -> new
                    CustomExceptionAuthor("Could not find author!"));
            Genre genre = Genre.valueOf(resultSet.getString("genre"));
            float price = resultSet.getFloat("price");

            Book book = new Book(bookId, title, author, genre, price);
            books.add(book);
        }
    }
}
