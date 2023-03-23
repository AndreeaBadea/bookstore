package com.softserve.bookstore.repositories;


import com.softserve.bookstore.connection.ConnectionManager;
import com.softserve.bookstore.data.ReadDataFromBookFile;
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
import java.util.Collections;
import java.util.List;

@Repository
public class BookRepository {

    public static final String INSERT_SQL = "INSERT INTO books (title,id_author,genre,price) VALUES (?,?,?,?)";
    public static final String QUERY = "SELECT * FROM author WHERE first_name = ? AND last_name = ? ";
    public static final String QUERY_AUTHORS = "INSERT INTO author( first_name,last_name) VALUES(?,?)";
    public static final String SELECT_LAST_AUTHORS = "SELECT TOP 3 * FROM author ORDER BY id_author DESC";

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

    //refactor!
    public Author getAuthorByName(List<Author> authors, String firstName, String lastName){
        for(Author author : authors){
            if(firstName.equals(author.getFirstName()) && lastName.equals(author.getLastName())){
                return author;
            }
        }
        return null;
    }


    public List<Author> findLastAuthorsAdded(int numberOfRecords) throws SQLException {
        Connection connection = connectionManager.getConnection();
        PreparedStatement authorStatement = connection.prepareStatement(SELECT_LAST_AUTHORS);
        ResultSet resultSet = authorStatement.executeQuery();
        List<Author> authors = new ArrayList<>();
        while(resultSet.next()){
            int authorId = resultSet.getInt("id_author");
            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");
            Author author = new Author(authorId, firstName, lastName);
            authors.add(author);
        }
        Collections.reverse(authors);
        return  authors;
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

        List<Author> authorsListFromFile = new ArrayList<>();

        for (Book book : bookList) {
            String firstName = book.getAuthor().getFirstName();
            String lastName = book.getAuthor().getLastName();
            authorsListFromFile.add(new Author(firstName, lastName));
        }

            addAuthors(authorsListFromFile);
            List<Author> lastAuthorsAdded = findLastAuthorsAdded(3);

            for(int i = 0; i < lastAuthorsAdded.size(); i++){
                bookStatement.setString(1, bookList.get(i).getTitle());
                Author author = getAuthorByName(lastAuthorsAdded,
                        bookList.get(i).getAuthor().getFirstName(),
                        bookList.get(i).getAuthor().getLastName());

                bookStatement.setInt(2, author.getIdAuthor());
                bookStatement.setString(3, String.valueOf(bookList.get(i).getGenre()));
                bookStatement.setFloat(4, bookList.get(i).getPrice());
                bookStatement.addBatch();
            }
            bookStatement.executeBatch();
    }

}
