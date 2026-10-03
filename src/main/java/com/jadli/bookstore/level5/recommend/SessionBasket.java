package com.jadli.bookstore.level5.recommend;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class SessionBasket {

    private final List<Long> bookIds = new ArrayList<>();

    public void add(Long bookId) {
        bookIds.add(bookId);
    }

    public List<Long> getBookIds() {
        return bookIds;
    }
}
