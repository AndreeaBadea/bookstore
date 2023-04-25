package com.softserve.bookstore.data;

import com.softserve.bookstore.generated.Author;
import com.softserve.bookstore.generated.Book;
import com.softserve.bookstore.generated.Genre;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Component
public class ReadDataFromBookFile {

    private List<Book> list = new ArrayList<>();



    public List<Book> readData (String file) throws IOException{
        try(FileReader fileReader = new FileReader(file);

        BufferedReader reader =  new BufferedReader(fileReader)){
            String line;

            while((line = reader.readLine()) !=null){

                String[] data = line.split(",(?=[^\\s])",5);
                int id = Integer.parseInt(data[0]);
                String title = data[1];
                Author author =  parseAuthorData(data[2]);
                Genre genre = Genre.valueOf(data[3]);
                float price = Float.parseFloat(data[4]);
                Book book = new Book(id,title,author,genre,price);
                list.add(book);

            }
        }
        return list;
    }

    public Author parseAuthorData(String authorString){
        String[] data =authorString.split("\\s+");
        String lastName = data[data.length-1];
        String[] firstNamesArray = Arrays.copyOf(data, data.length - 1);
        String firstName = String.join(" ", firstNamesArray);
        return new Author( firstName, lastName);


    }



}
