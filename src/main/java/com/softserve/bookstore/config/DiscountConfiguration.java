package com.softserve.bookstore.config;

import com.softserve.bookstore.generated.Genre;
import com.softserve.bookstore.threads.DiscountScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

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
