package com.softserve.bookstore.generated;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "message"
})
@XmlRootElement(name = "getBookResponse", namespace = "http://www.softserve.com/bookstore/generated")
public class DeleteBookResponse {

    @XmlElement(required = true)
    protected String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
