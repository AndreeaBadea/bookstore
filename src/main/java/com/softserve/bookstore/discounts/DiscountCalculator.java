package com.softserve.bookstore.discounts;

import com.softserve.bookstore.generated.Book;
import com.softserve.bookstore.generated.Genre;
import com.softserve.bookstore.repositories.BookRepository;
import com.softserve.bookstore.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tinylog.Logger;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class DiscountCalculator {

    @Autowired
    private BookRepository bookRepository;

    private float calculateDiscount(float price, float discountPercentage) {
        float discountAmount = price * (discountPercentage / 100);
        return price - discountAmount;
    }


    public List<Book> applyDiscountOnBooks(Genre bookGenre, int numberOfBooks) throws SQLException {
        List<Book> booksOfGenre = bookRepository.findAll().parallelStream()
                .filter(book -> book.getGenre().equals(bookGenre))
                .collect(Collectors.toList());
        Logger.info("wtf");
        System.out.println(booksOfGenre.size());
        List<Book> selectedBooks =  selectRandomBooks(booksOfGenre, numberOfBooks);

       for(int i = 0; i < selectedBooks.size(); i++){
           System.out.println("In fooorrr");
           float currentPrice = selectedBooks.get(i).getPrice();
           float priceAfterDiscount = calculateDiscount(currentPrice, 35);
           selectedBooks.get(i).setPrice(priceAfterDiscount);
           Logger.info("{} is now at a discount price of {}. ", selectedBooks.get(i).getTitle(), selectedBooks.get(i).getPrice() );
       }
        return selectedBooks;
    }

    public List<Book> selectRandomBooks(List<Book> booksList, int numberOfBooks){
        Random random = new Random();
        List<Book> randomBooks = new ArrayList<>();
        Set<Integer> randomIndexes = new HashSet<>();

        if(booksList.size() < numberOfBooks) {
            return booksList;
        }

        while(randomIndexes.size() < numberOfBooks){
            int randomIndex = random.nextInt(booksList.size());
            randomIndexes.add(randomIndex);
        }

        for(int index : randomIndexes){
            randomBooks.add(booksList.get(index));
        }

        return randomBooks;
    }
}