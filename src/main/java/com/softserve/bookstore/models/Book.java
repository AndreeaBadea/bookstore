package com.softserve.bookstore.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Book {
    public static final String FIELD_BOOK_ID = "id_author";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_AUTHOR = "author";
    public static final String FIELD_FIRSTNAME = "first_name";
    public static final String FIELD_LASTNAME = "last_name";
    public static final String FIELD_GENRE = "genre";
    public static final String FIELD_PRICES = "price";


    @Id
    private int idBook;
    private String title;
    private Author author;
    private Genre genre;
    private float price;

}
