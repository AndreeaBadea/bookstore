package com.softserve.bookstore.models;

import java.sql.SQLException;

public interface Observable {

    void subscribe(Observer observer) throws SQLException;
    void unsubscribe(Observer observer) throws SQLException;
    void notifyObservers(String message) throws SQLException;

}
