package com.jadli.bookstore.level4.pricing;

import com.jadli.bookstore.domain.Book;
import java.time.LocalDate;
import org.springframework.stereotype.Service;

@Service
public class PricingService {

    public double calculatePrice(Book book, int quantity, String customerTier, String couponCode,
                                 String region, boolean giftWrap, boolean express, LocalDate orderDate) {
        double price = book.getPrice() * quantity;
        if (customerTier != null) {
            if (customerTier.equals("GOLD")) {
                if (quantity >= 10) {
                    price = price * 0.80;
                } else if (quantity >= 5) {
                    price = price * 0.85;
                } else {
                    price = price * 0.90;
                }
            } else if (customerTier.equals("SILVER")) {
                if (quantity >= 10) {
                    price = price * 0.85;
                } else if (quantity >= 5) {
                    price = price * 0.90;
                } else {
                    price = price * 0.95;
                }
            } else if (customerTier.equals("STUDENT")) {
                if (book.getGenre() != null && book.getGenre().equals("programming")) {
                    if (quantity > 1) {
                        price = price * 0.85;
                    } else {
                        price = price * 0.90;
                    }
                } else {
                    price = price * 0.95;
                }
            } else if (customerTier.equals("STAFF")) {
                price = price * 0.70;
            }
        }
        if (orderDate != null) {
            int month = orderDate.getMonthValue();
            if (month == 12) {
                if (orderDate.getDayOfMonth() <= 24) {
                    price = price * 0.95;
                } else {
                    price = price * 0.90;
                }
            } else if (month == 9 || month == 10) {
                if (book.getGenre() != null && book.getGenre().equals("programming")) {
                    price = price * 0.92;
                }
            } else if (month == 6 || month == 7 || month == 8) {
                for (int i = 0; i < quantity; i++) {
                    if (i % 3 == 2) {
                        price = price - book.getPrice() * 0.10;
                    }
                }
            }
        }
        if (couponCode != null && !couponCode.isEmpty()) {
            if (couponCode.startsWith("WELCOME")) {
                if (price > 20) {
                    price = price - 5;
                } else {
                    price = price - 2;
                }
            } else if (couponCode.startsWith("BULK")) {
                if (quantity >= 20) {
                    price = price * 0.75;
                } else if (quantity >= 10) {
                    price = price * 0.85;
                }
            } else if (couponCode.startsWith("FRIEND")) {
                if (price > 20) {
                    price = price - 5;
                } else {
                    price = price - 2;
                }
            } else if (couponCode.equals("FREESHIP")) {
                express = false;
            }
        }
        double tax = 0;
        switch (region == null ? "" : region) {
            case "LT":
                tax = 0.21;
                break;
            case "LV":
                tax = 0.21;
                break;
            case "EE":
                tax = 0.22;
                break;
            case "DE":
                tax = 0.19;
                break;
            case "FR":
                tax = 0.20;
                break;
            case "US":
                tax = 0;
                break;
            default:
                tax = 0.21;
        }
        price = price * (1 + tax);
        if (giftWrap) {
            price += quantity > 3 ? (quantity > 6 ? 2.0 : 3.5) : 5.0;
        }
        if (express) {
            if (region != null && (region.equals("LT") || region.equals("LV") || region.equals("EE"))) {
                price += 4.99;
            } else if (region != null && (region.equals("DE") || region.equals("FR"))) {
                price += 9.99;
            } else {
                price += 24.99;
            }
        }
        if (price < 0) {
            price = 0;
        }
        return Math.round(price * 100.0) / 100.0;
    }
}
