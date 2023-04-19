package com.softserve.bookstore.models;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Genre")
public enum Genre {

    FICTION("Fiction"),
    DRAMA("Drama"),
    ROMANCE("Romance"),
    BIOGRAPHY("Biography"),
    HORROR("Horror");


    private final String name;

    Genre(String name) {
        this.name = name;
    }

    public String getGenreName() {
        return name;
    }

}
