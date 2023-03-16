package com.softserve.bookstore.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum Status {

    PENDING,

    IN_PROCESS,

    COMPLETED;

    private String description;

}
