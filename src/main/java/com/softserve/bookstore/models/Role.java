package com.softserve.bookstore.models;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Role")
public enum Role {

    USER,
    ADMIN
}
