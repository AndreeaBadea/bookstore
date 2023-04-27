package com.softserve.bookstore.generated;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "bookDto"
})
@XmlRootElement(name = "addBooksResponse")
public class AddBooksResponse {

    @XmlElement(required = true)
    protected BookDto bookDto;

    /**
     * Gets the value of the userDto property.
     *
     * @return
     *     possible object is
     *     {@link BookDto }
     *
     */
    public BookDto getBookDto() {
        return bookDto;
    }

    /**
     * Sets the value of the userDto property.
     *
     * @param value
     *     allowed object is
     *     {@link BookDto }
     *
     */
    public void setBookDto(BookDto value) {
        this.bookDto = value;
    }

}
