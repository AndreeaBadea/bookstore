package com.softserve.bookstore.repositories;


import com.softserve.bookstore.connection.ConnectionManager;
import com.softserve.bookstore.data.ReadDataFromBookFile;
import com.softserve.bookstore.data.utils.AuthorUtility;

import com.softserve.bookstore.models.Author;
import com.softserve.bookstore.models.Book;
import com.softserve.bookstore.models.Genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.tinylog.Logger;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Repository
public class BookRepository {

    @Autowired
    private AuthorRepository authorRepository;


    public static final String INSERT_SQL = "INSERT INTO books (title,id_author,genre,price) VALUES (?,?,?,?)";
    public static final String QUERY = "SELECT * FROM author WHERE first_name = ? AND last_name = ? ";
    public static final String SELECT_LAST_AUTHORS = "SELECT TOP 3 * FROM author ORDER BY id_author DESC";
    public static final String QUERY_AUTHORS = "INSERT INTO author( first_name,last_name) VALUES(?,?)";
    public static final String SELECT_BOOKS = "SELECT * FROM books";
    public static final String SELECT_AUTHORS = "SELECT * FROM author";
    @Autowired
    private ReadDataFromBookFile readDataFromBookFile;

    @Autowired
    private ConnectionManager connectionManager;

    public List<Book> findBooks(String fileName) throws IOException {
        return readDataFromBookFile.readData(fileName);
    }

    public Optional<Author> getAuthorById(List<Author> authors, int id) {
        Optional<Author> a = Optional.of(new Author());

        for (Author author : authors) {

            if (author.getIdAuthor() == id) a = Optional.of(author);
        }
        return a;
    }

    private List<Author> getAuthorFromResultsSet(ResultSet resultSet) throws SQLException {
        List<Author> authorsList = new ArrayList<>();

        while (resultSet.next()) {

            int authorId = resultSet.getInt("id");
            String firstName = resultSet.getString("firstname");
            String lastName = resultSet.getString("lastname");

            Author author = new Author(authorId, firstName, lastName);
            authorsList.add(author);
        }
        return authorsList;

    }

    public List<Book> getBookList(ResultSet resultSet) throws SQLException {
        List<Book> books = new ArrayList<>();
        List<Author> authors;
        Connection connection = connectionManager.getConnection();
        PreparedStatement authorStatement = connection.prepareStatement(SELECT_AUTHORS);
        ResultSet aut = authorStatement.executeQuery();
        authors = getAuthorFromResultsSet(aut);


        while (resultSet.next()) {

            int bookId = resultSet.getInt("id");
            String title = resultSet.getString("title");
            int idAuthor = resultSet.getInt("author");
            Author author = getAuthorById(authors, idAuthor).get();
            Genre genre = Genre.valueOf("FICTION");
            float price = resultSet.getFloat("price");


            Book book = new Book(bookId, title, author, genre, price);
            books.add(book);

        }
        return books;
    }


    public List<Book> findAll() throws SQLException {
        Connection connection = connectionManager.getConnection();
        PreparedStatement bookStatement = connection.prepareStatement(SELECT_BOOKS);
        ResultSet resultSet = bookStatement.executeQuery();
        Logger.info("Books were successfully retrived from the database.");
        return getBookList(resultSet);
    }

    public List<Author> findAuthorByName(String firstName, String lastName) throws SQLException {
        Connection con = connectionManager.getConnection();

        PreparedStatement preparedStatement = con.prepareStatement(QUERY);

        preparedStatement.setString(1,firstName);
        preparedStatement.setString(2,lastName);
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
        Connection connection = connectionManager.getConnection();
        PreparedStatement authorStatement = connection.prepareStatement(SELECT_LAST_AUTHORS);
        ResultSet resultSet = authorStatement.executeQuery();

        List<Author> authors = new ArrayList<>();

        return AuthorRepository.AuthorUtilitys.getAuthors(resultSet, authors);
    }


    public void addAuthors(List<Author> authors) throws SQLException {
        Connection con = connectionManager.getConnection();
        PreparedStatement statement = con.prepareStatement(QUERY_AUTHORS);

        authors.forEach(author -> AuthorUtility.addToBatch(statement, author));
        statement.executeBatch();

    }


    public void addBook(List<Book> bookList) throws SQLException {

        Connection con = connectionManager.getConnection();
        PreparedStatement bookStatement = con.prepareStatement(INSERT_SQL);
        List<Author> authorsListFromFile = new ArrayList<>();

        AuthorRepository.addingAuthorsFromList(bookList, authorsListFromFile);

        addAuthors(authorsListFromFile);

        List<Author> lastAuthorsAdded = findLastAuthorsAdded(3);

        authorRepository.addingBooksAndAuthorId(bookList, bookStatement, lastAuthorsAdded);
        bookStatement.executeBatch();


    }
}