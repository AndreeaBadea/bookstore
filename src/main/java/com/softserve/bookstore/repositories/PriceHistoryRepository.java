package com.softserve.bookstore.repositories;

import com.softserve.bookstore.connection.ConnectionManager;
import com.softserve.bookstore.models.PriceHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.sql.*;

@Repository
public class PriceHistoryRepository {

    public static final String INSERT_PRICE_HISTORY =
            "INSERT INTO prices_history(id_book, previous_price, change_date) VALUES(?,?,?)";

    @Autowired
    private ConnectionManager connectionManager;

    private Connection connection;

    @PostConstruct
    public void init() throws SQLException {
        connection = connectionManager.getConnection();
    }

    public PriceHistory addPriceHistory(PriceHistory priceHistory) throws SQLException {
        PreparedStatement priceHistoryStatement = connection.prepareStatement(INSERT_PRICE_HISTORY, Statement.RETURN_GENERATED_KEYS);
        priceHistoryStatement.setInt(1, priceHistory.getIdBook());
        priceHistoryStatement.setFloat(2, priceHistory.getPreviousPrice());
        priceHistoryStatement.setDate(3, priceHistory.getChangeDate());
        priceHistoryStatement.executeUpdate();

        ResultSet generatedKeys = priceHistoryStatement.getGeneratedKeys();
        if(generatedKeys.next()){
            int priceHistoryId = generatedKeys.getInt(1);
            priceHistory.setIdPriceHistory(priceHistoryId);
        }

        return priceHistory;
    }


}
