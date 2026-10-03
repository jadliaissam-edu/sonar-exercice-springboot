package com.jadli.bookstore.level5.config;

import com.jadli.bookstore.level5.mail.ReceiptPrinter;
import com.jadli.bookstore.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableAsync
@EnableScheduling
public class SchedulingConfig {

    @Autowired
    private BookRepository bookRepository;

    @Bean(name = "Receipt_Printer")
    public ReceiptPrinter receiptPrinter() {
        return new ReceiptPrinter(bookRepository);
    }
}
