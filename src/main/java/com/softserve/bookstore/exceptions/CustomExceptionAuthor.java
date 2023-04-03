package com.softserve.bookstore.exceptions;

public class CustomExceptionAuthor extends RuntimeException {
    public CustomExceptionAuthor (String msg){
        super(msg);
    }
}
