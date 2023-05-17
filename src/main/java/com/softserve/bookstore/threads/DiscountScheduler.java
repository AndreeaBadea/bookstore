package com.softserve.bookstore.threads;

import com.softserve.bookstore.discounts.DiscountCalculator;
import com.softserve.bookstore.generated.Book;
import com.softserve.bookstore.generated.Genre;
import com.softserve.bookstore.models.Newsletter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tinylog.Logger;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class DiscountScheduler {

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();


    @Autowired
    private DiscountCalculator discountCalculator;

    @Autowired
    private Newsletter newsletter;

    public void scheduleDiscount() {
        Runnable task = () -> {
           try {

               List<Book> bookWithDiscount = discountCalculator.applyDiscountOnBooks(Genre.DRAMA, 5);
               Logger.info("Discount applied for {} books of {}.", bookWithDiscount.size(), Genre.DRAMA);
               newsletter.notifyObservers("NEWSLETTER: Discount of 35% at books in " + Genre.DRAMA + " category");

           } catch(Exception e){
               e.printStackTrace();
           }
        };
        scheduledExecutorService.scheduleAtFixedRate(task, 10,30, TimeUnit.SECONDS);
    }
}
