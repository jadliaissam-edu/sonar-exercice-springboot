package com.jadli.bookstore.level1;

import com.jadli.bookstore.domain.Book;
import java.util.List;

public class Book_Formatter {

    public static final String default_currency = "EUR";

    private String Last_Title;

    public String Format_Line(Book book) {
        String Line_Text = book.getTitle() + " by " + book.getAuthor();
        Last_Title = book.getTitle();
        return Line_Text;
    }

    public String Format_Price(Book book) {
        return String.format("%.2f %s", book.getPrice(), default_currency);
    }

    public String joinTitles(List<Book> books) {
        StringBuilder sb = new StringBuilder();
        for (Book b : books) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(b.getTitle());
        }
        return sb.toString();
    }

    public String lastFormattedTitle() {
        return Last_Title;
    }
}
