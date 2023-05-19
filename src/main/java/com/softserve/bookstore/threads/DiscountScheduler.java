package com.softserve.bookstore.threads;

import com.softserve.bookstore.config.DiscountParametersConfig;
import com.softserve.bookstore.discounts.DiscountCalculator;
import com.softserve.bookstore.exceptions.PriceHistoryException;
import com.softserve.bookstore.generated.Book;
import com.softserve.bookstore.models.Newsletter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tinylog.Logger;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class DiscountScheduler {


    private final ScheduledExecutorService discountExecutorService = Executors.newSingleThreadScheduledExecutor();

    private final ScheduledExecutorService restorePriceExecutorService = Executors.newSingleThreadScheduledExecutor();
    
    private List<Book> booksWithDiscount;

    @Autowired
    private DiscountCalculator discountCalculator;

    @Autowired
    private DiscountParametersConfig discountParametersConfig;

    @Autowired
    private Newsletter newsletter;

    public void scheduleDiscount() {
        Runnable task = () -> {
           try {
               booksWithDiscount = discountCalculator.applyDiscountOnBooks(
                       discountParametersConfig.GENRE,
                       discountParametersConfig.MAX_NUMBER_OF_BOOKS,
                       discountParametersConfig.DISCOUNT_PERCENTAGE);

               if(booksWithDiscount.size() > 0) {
                   Logger.info("Discount applied for {} books of {}.", booksWithDiscount.size(), discountParametersConfig.GENRE);
                   newsletter.notifyObservers("NEWSLETTER: Discount of " + discountParametersConfig.DISCOUNT_PERCENTAGE +
                           "% at books in " + discountParametersConfig.GENRE + " category");
                   schedulePriceRestoration(booksWithDiscount, discountParametersConfig.DISCOUNT_DURATION);
               } else{
                   Logger.info("There are no books available for discount in this category.");
               }

           } catch(SQLException e){
               Logger.error(e.getMessage());
           }
        };
        discountExecutorService.scheduleAtFixedRate(task, 20,30, TimeUnit.SECONDS);
    }
    
    public void schedulePriceRestoration(List<Book> booksWithDiscount, int discountDuration) {
        for(Book book : booksWithDiscount) {
            Runnable task = () -> {
                try {
                    discountCalculator.restoreBookPrice(book);
                } catch (PriceHistoryException | SQLException e) {
                    Logger.error(e.getMessage());
                }
                Logger.info("Discount period is over for book {} ", book.getTitle());
            };
            restorePriceExecutorService.schedule(task,  discountDuration, TimeUnit.SECONDS);
        }
    }

}
