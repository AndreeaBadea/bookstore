package com.softserve.bookstore.models.dtos;

import com.softserve.bookstore.models.Author;
import com.softserve.bookstore.models.Genre;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "BookDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class BookDto {

    private int idBook;

    private String title;

    @XmlElement(name = "author")
    private Author author;

    @XmlElement(name = "genre")
    private Genre genre;

    private float price;
}
