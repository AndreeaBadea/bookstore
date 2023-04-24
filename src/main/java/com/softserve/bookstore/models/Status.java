package com.softserve.bookstore.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "Status")
public enum Status {

    PENDING,

    IN_PROCESS,

    COMPLETED;

    private String description;

}
