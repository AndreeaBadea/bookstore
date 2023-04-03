package com.softserve.bookstore.data.utils;

import com.softserve.bookstore.data.ReadDataFromBookFile;
import com.softserve.bookstore.models.Book;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

public class BookUtils {

    @Autowired
    private ReadDataFromBookFile readDataFromBookFile;


    public List<Book> findBooks(String fileName) throws IOException {
        return readDataFromBookFile.readData(fileName);
    }
}
