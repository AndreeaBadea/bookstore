package com.softserve.bookstore.config;

import com.softserve.bookstore.generated.Genre;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableConfigurationProperties
@PropertySource("classpath:application.properties")
@Getter
public class DiscountParametersConfig {

    @Value("${discountDuration}")
    public int discountDuration;

    @Value("${maxNumberOfBooks}")
    public int maxNumberOfBooks;

    @Value("${bookGenre}")
    public Genre genre;

    @Value("${discountPercentage}")
    public int discountPercentage;

}
