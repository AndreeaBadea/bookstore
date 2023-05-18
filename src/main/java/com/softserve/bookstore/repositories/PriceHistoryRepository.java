package com.softserve.bookstore.repositories;

import com.softserve.bookstore.connection.ConnectionManager;
import com.softserve.bookstore.models.PriceHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PriceHistoryRepository {

    public static final String INSERT_PRICE_HISTORY =
            "INSERT INTO prices_history(id_book, previous_price, change_date) VALUES(?,?,?)";
    public static final String SELECT_PRICES_HISTORY = "SELECT * FROM prices_history";
    public static final String DELETE_PRICE_HISTORY_BY_BOOK_ID = "DELETE FROM prices_history WHERE id_book = ?";

    @Autowired
    private ConnectionManager connectionManager;

    private Connection connection;

    @PostConstruct
    public void init() throws SQLException {
        connection = connectionManager.getConnection();
    }

    public List<PriceHistory> findAll() throws SQLException {
        PreparedStatement priceHistoryStatement = connection.prepareStatement(SELECT_PRICES_HISTORY);
        ResultSet resultSet = priceHistoryStatement.executeQuery();
        return PriceHistoryUtil.getPriceHistoryFromResultSet(resultSet);
    }

    public PriceHistory addPriceHistory(PriceHistory priceHistory) throws SQLException {
        PreparedStatement priceHistoryStatement = connection.prepareStatement(INSERT_PRICE_HISTORY, Statement.RETURN_GENERATED_KEYS);
        priceHistoryStatement.setInt(1, priceHistory.getIdBook());
        priceHistoryStatement.setFloat(2, priceHistory.getPreviousPrice());
        priceHistoryStatement.setDate(3, priceHistory.getChangeDate());
        priceHistoryStatement.executeUpdate();

        ResultSet generatedKeys = priceHistoryStatement.getGeneratedKeys();
        if (generatedKeys.next()) {
            int priceHistoryId = generatedKeys.getInt(1);
            priceHistory.setIdPriceHistory(priceHistoryId);
        }

        return priceHistory;
    }

    public Optional<PriceHistory> getPriceHistoryByBookId(int bookId) throws SQLException {
        return  findAll().stream()
                .filter(priceHistory -> bookId == priceHistory.getIdBook())
                .findFirst();
    }

    public int deletePriceHistoryByBookId(int bookId) throws SQLException {
       PreparedStatement priceHistoryStatement = connection.prepareStatement(DELETE_PRICE_HISTORY_BY_BOOK_ID);
       priceHistoryStatement.setInt(1, bookId);
       return priceHistoryStatement.executeUpdate();

    }
}
class PriceHistoryUtil {

    private PriceHistoryUtil() { }

    public static List<PriceHistory> getPriceHistoryFromResultSet(ResultSet resultSet) throws SQLException {
        List<PriceHistory> pricesHistory = new ArrayList<>();
        while (resultSet.next()) {
            int priceHistoryId = resultSet.getInt(PriceHistory.FIELD_PRICE_HISTORY_ID);
            int bookId = resultSet.getInt(PriceHistory.FIELD_BOOK_ID);
            float previousPrice = resultSet.getFloat(PriceHistory.FIELD_PREVIOUS_PRICE);
            Date changeDate = resultSet.getDate(PriceHistory.FIELD_CHANGE_DATE);
            PriceHistory priceHistory = new PriceHistory(priceHistoryId, bookId, previousPrice, changeDate);
            pricesHistory.add(priceHistory);
        }
        return pricesHistory;
    }


}
