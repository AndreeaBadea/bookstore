package com.softserve.bookstore.repositories;

import com.softserve.bookstore.models.Author;
import com.softserve.bookstore.models.Book;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Repository
public class AuthorRepository {

    public Author getAuthorByName(List<Author> authors, String firstName, String lastName){
        for(Author author : authors){
            if(firstName.equals(author.getFirstName()) && lastName.equals(author.getLastName())){
                return author;
            }
        }
        return null;
    }
    public void addingBooksAndAuthorId(List<Book> bookList, PreparedStatement bookStatement, List<Author> lastAuthorsAdded) throws SQLException {
        for (int i = 0; i < lastAuthorsAdded.size(); i++) {
            bookStatement.setString(1, bookList.get(i).getTitle());
            Author author = getAuthorByName(lastAuthorsAdded,
                    bookList.get(i).getAuthor().getFirstName(),
                    bookList.get(i).getAuthor().getLastName());

            bookStatement.setInt(2, author.getIdAuthor());
            bookStatement.setString(3, String.valueOf(bookList.get(i).getGenre()));
            bookStatement.setFloat(4, bookList.get(i).getPrice());
            bookStatement.addBatch();
        }
    }

     static void addingAuthorsFromList(List<Book> bookList, List<Author> authorsListFromFile) {
        for (Book book : bookList) {
            String firstName = book.getAuthor().getFirstName();
            String lastName = book.getAuthor().getLastName();
            authorsListFromFile.add(new Author(firstName, lastName));
        }
    }

   static class AuthorUtilitys{
        public static List<Author> getAuthors(ResultSet resultSet, List<Author> authors) throws SQLException {
            while(resultSet.next()){
                int authorId = resultSet.getInt("author");
                String firstName = resultSet.getString("firstname");
                String lastName = resultSet.getString("lastname");
                Author author = new Author(authorId, firstName, lastName);
                authors.add(author);
            }
            Collections.reverse(authors);
            return authors;
        }
    }

}
