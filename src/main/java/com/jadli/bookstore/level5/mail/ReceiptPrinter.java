package com.jadli.bookstore.level5.mail;

import com.jadli.bookstore.repository.BookRepository;

public class ReceiptPrinter {

    private final BookRepository bookRepository;

    public ReceiptPrinter(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public String titleOf(Long bookId) {
        return bookRepository.findById(bookId).map(b -> b.getTitle()).orElse("unknown title");
    }
}
