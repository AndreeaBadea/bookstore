package com.softserve.bookstore.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public PasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain (HttpSecurity httpSecurity) throws Exception{
        httpSecurity.csrf().disable();
        return httpSecurity.build();
    }

    @Bean
    UserDetailsService userDetailsService (PasswordEncoder encoder){
        var userDetailsService = new InMemoryUserDetailsManager();

        UserDetails admin = User.builder().username("admin")
                .passwordEncoder(encoder::encode)
                .password("admin").roles("ADMIN").build();

        userDetailsService.createUser(admin);

        UserDetails user = User.builder().username("user")
                .passwordEncoder(encoder::encode)
                .password("user").roles("USER").build();

        userDetailsService.createUser(user);

        return userDetailsService;
    }
}
