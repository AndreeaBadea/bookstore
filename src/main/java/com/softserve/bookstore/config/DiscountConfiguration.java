package com.softserve.bookstore.config;

import com.softserve.bookstore.threads.DiscountScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class DiscountConfiguration {

    @Autowired
    private DiscountScheduler discountScheduler;

    @PostConstruct
    public void init(){
        discountScheduler.scheduleDiscount();
    }

}
