package com.softserve.bookstore.models;

import com.softserve.bookstore.generated.User;
import com.softserve.bookstore.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tinylog.Logger;

import java.sql.SQLException;

@Component
public class Newsletter implements Observable {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void subscribe(Observer observer) throws SQLException {
        User user = (User) observer;
        userRepository.subscribeToNewsletter(user.getUserId());
        Logger.info("{} subscribed to newsletter.", user.getEmail());
    }

    @Override
    public void unsubscribe(Observer observer) throws SQLException {
        User user = (User) observer;
        userRepository.unsubscribeFromNewsletter(user.getUserId());
        Logger.info("{} unsubscribed from newsletter.", user.getEmail());

    }

    @Override
    public void notifyObservers(String message) throws SQLException {
         userRepository.findAll().stream()
                 .filter(User::isSubscribed)
                 .forEach(observer -> observer.notifyObserver(message));
    }
}
