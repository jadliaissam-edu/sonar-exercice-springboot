package com.jadli.bookstore.level5.legacy;

import com.jadli.bookstore.domain.Book;
import java.io.Serializable;

public class CartSnapshot implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String owner;

    private final Book lastAdded;

    public CartSnapshot(String owner, Book lastAdded) {
        this.owner = owner;
        this.lastAdded = lastAdded;
    }

    public String getOwner() {
        return owner;
    }

    public Book getLastAdded() {
        return lastAdded;
    }
}
