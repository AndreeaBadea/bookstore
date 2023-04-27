package com.softserve.bookstore.generated;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="book" type="{http://www.softserve.com/bookstore/generated}book"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "book"
})
@XmlRootElement(name = "addBooksRequest")
public class AddBooksRequest {

    @XmlElement(required = true)
    protected Book book;

    /**
     * Gets the value of the book property.
     *
     * @return
     *     possible object is
     *     {@link Book }
     *
     */
    public Book getBook() {
        return book;
    }

    /**
     * Sets the value of the book property.
     *
     * @param value
     *     allowed object is
     *     {@link Book }
     *
     */
    public void setBook(Book value) {
        this.book = value;
    }

}
