package com.softserve.bookstore.models;

public enum Genre {

    FICTION("Fiction"),
    DRAMA("Drama"),
    ROMANCE("Romance"),
    BIOGRAPHY("Biography"),
    HORROR("Horror");


    private final String name;

    Genre(String name){
        this.name = name;

    }

    public String getGenreName(){
        return name;
    }

//    public static Genre getByUpperCase(String name){
//        if (name==null||name.isEmpty()){
//            return null;
//        }
//        return Genre.valueOf(name.toUpperCase());
//    }

}
