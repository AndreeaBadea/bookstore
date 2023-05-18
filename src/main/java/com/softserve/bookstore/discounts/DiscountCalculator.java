package com.softserve.bookstore.discounts;

import com.softserve.bookstore.exceptions.PriceHistoryException;
import com.softserve.bookstore.generated.Book;
import com.softserve.bookstore.generated.Genre;
import com.softserve.bookstore.models.PriceHistory;
import com.softserve.bookstore.repositories.BookRepository;
import com.softserve.bookstore.repositories.PriceHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tinylog.Logger;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Component
public class DiscountCalculator {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private PriceHistoryRepository priceHistoryRepository;

    private float calculateDiscount(float price, float discountPercentage) {
        float discountAmount = price * (discountPercentage / 100);
        return price - discountAmount;
    }


    public List<Book> applyDiscountOnBooks(Genre bookGenre, int maxNumberOfBooks, int discountPercentage) throws SQLException {
        List<Book> booksOfGenre = bookRepository.findBooksByGenre(bookGenre);
        List<Book> selectedBooks = selectRandomBooks(booksOfGenre, maxNumberOfBooks);

        for (Book book : selectedBooks) {
            float currentPrice = book.getPrice();
            float priceAfterDiscount = calculateDiscount(currentPrice, discountPercentage);

            PriceHistory priceHistory = new PriceHistory(book.getIdBook(), currentPrice, Date.valueOf(LocalDate.now()));
            priceHistoryRepository.addPriceHistory(priceHistory);


            book.setPrice(priceAfterDiscount);
            bookRepository.updateBookPrice(book.getIdBook(), priceAfterDiscount);

            Logger.info("{} is now at a discount price of {}. ", book.getTitle(), book.getPrice());
        }

        return selectedBooks;
    }


    public void restoreBookPrice(Book book) throws SQLException, PriceHistoryException {
        PriceHistory priceHistory = priceHistoryRepository.getPriceHistoryByBookId(
                book.getIdBook()).orElseThrow(() -> new PriceHistoryException("There is no previous price for book id " + book.getIdBook()));

        if (priceHistory != null) {
            float previousBookPrice = priceHistory.getPreviousPrice();
            book.setPrice(previousBookPrice);

            bookRepository.updateBookPrice(book.getIdBook(), previousBookPrice);
            priceHistoryRepository.deletePriceHistoryByBookId(book.getIdBook());
        }
    }


    private List<Book> getEligibleBooksForDiscount(List<Book> booksList) throws SQLException {
        List<Book> eligibleBooks = new ArrayList<>();
        for (Book book : booksList) {
            Optional<PriceHistory> optionalPriceHistory = priceHistoryRepository.getPriceHistoryByBookId(book.getIdBook());
            float previousPrice = 0;

            if (optionalPriceHistory.isPresent()) {
                previousPrice = optionalPriceHistory.get().getPreviousPrice();
            }

            if (previousPrice > book.getPrice()) {
                Logger.info("{} already has a discount applied. Skipping..", book.getTitle());
                continue;
            }
            eligibleBooks.add(book);
        }
        return eligibleBooks;
    }


    public List<Book> selectRandomBooks(List<Book> booksList, int numberOfBooks) throws SQLException {
        Random random = new Random();
        List<Book> randomBooks = new ArrayList<>();
        List<Book> eligibleBooks = getEligibleBooksForDiscount(booksList);

        if (eligibleBooks.size() <= numberOfBooks) {
            return eligibleBooks;
        }

        int attempts = 0;
        while (randomBooks.size() < numberOfBooks && attempts <= eligibleBooks.size()) {
            int randomIndex = random.nextInt(eligibleBooks.size());
            Book randomBook = eligibleBooks.get(randomIndex);

            if (!randomBooks.contains(randomBook)) {
                randomBooks.add(randomBook);
            }

            attempts++;
        }
        return randomBooks;
    }

}