package com.softserve.bookstore.models.dtos.mappers;

import com.softserve.bookstore.generated.Book;
import com.softserve.bookstore.generated.BookDto;

public class BookMapper {


    public static BookDto toBookDto (Book book){

        BookDto bookDto = new BookDto();

        bookDto.setIdBook(book.getIdBook());
        bookDto.setTitle(bookDto.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setGenre(book.getGenre());
        bookDto.setPrice(bookDto.getPrice());
        return bookDto;
    }
}
