package com.softserve.bookstore.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PriceHistory {
    public static final String FIELD_PRICE_HISTORY_ID = "id_price_history";
    public static final String FIELD_BOOK_ID = "id_book";
    public static final String FIELD_PREVIOUS_PRICE = "previous_price";
    public static final String FIELD_CHANGE_DATE = "change_date";

    @Id
    private int idPriceHistory;

    private int idBook;

    private float previousPrice;

    private Date changeDate;

    public PriceHistory(int idBook, float previousPrice, Date changeDate) {
        this.idBook = idBook;
        this.previousPrice = previousPrice;
        this.changeDate = changeDate;
    }

    @Override
    public String toString() {
        return "PriceHistory{" +
                "idPriceHistory=" + idPriceHistory +
                ", idBook=" + idBook +
                ", previousPrice=" + previousPrice +
                ", changeDate=" + changeDate +
                '}';
    }
}
