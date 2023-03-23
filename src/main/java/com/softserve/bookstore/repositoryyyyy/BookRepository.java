package com.softserve.bookstore.repositoryyyyy;


import com.softserve.bookstore.connection.ConnectionManager;
import com.softserve.bookstore.connection.ReadDataFromBookFile;
import com.softserve.bookstore.models.Author;
import com.softserve.bookstore.models.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class BookRepository {

    public static final String INSERT_SQL = "INSERT INTO books (title,id_author,genre,price) VALUES (?,?,?,?)";
    public static final String QUERY = "SELECT * FROM author WHERE first_name = ? AND last_name = ? ";
    public static final String QUERY_AUTHORS = "INSERT INTO author( first_name,last_name) VALUES(?,?)";
    @Autowired
    private ReadDataFromBookFile readDataFromBookFile;

    @Autowired
    ConnectionManager connectionManager;


    public List<Book>findBooks(String fileName) throws IOException {
        return readDataFromBookFile.readData(fileName);
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
      private static void addToBatch(PreparedStatement statement, Author author) {

        try {

            statement.setString(1,author.getFirstName());
            statement.setString(2,author.getLastName());
            statement.addBatch();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }


    public void addAuthors(List<Author> authors) throws SQLException{
        Connection con = connectionManager.getConnection();
        PreparedStatement statement = con.prepareStatement(QUERY_AUTHORS);

        authors.forEach(author -> {
            addToBatch(statement,author);
        });
        statement.executeBatch();

    }


    public void addBook( List<Book>bookList) throws SQLException {

        Connection con = connectionManager.getConnection();

        PreparedStatement bookStatement = con.prepareStatement(INSERT_SQL);

        for (Book book : bookList) {

            String firstName = book.getAuthor().getFirstName();
            String lastName = book.getAuthor().getLastName();
            int idAuthor = book.getAuthor().getIdAuthor();


            List<Author> authorList = findAuthorByName(firstName, lastName);
            authorList.add(new Author(idAuthor,firstName,lastName));
            addAuthors(authorList);
            System.out.println(authorList);


            bookStatement.setString(1, book.getTitle());
            bookStatement.setInt(2, book.getAuthor().getIdAuthor());
            bookStatement.setString(3, String.valueOf(book.getGenre()));
            bookStatement.setFloat(4, book.getPrice());
            bookStatement.addBatch();

        }
            bookStatement.executeBatch();

    }

}
