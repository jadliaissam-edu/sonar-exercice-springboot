package com.jadli.bookstore.level4.pricing;

import org.springframework.stereotype.Service;

@Service
public class DiscountService {

    public double loyaltyDiscount(double amount) {
        if (amount > 100) {
            return amount * 0.10;
        }
        if (amount > 50) {
            return amount * 0.05;
        }
        return 0;
    }

    public double studentDiscount(double amount) {
        if (amount > 100) {
            return amount * 0.10;
        }
        if (amount > 50) {
            return amount * 0.05;
        }
        return 0;
    }

    public double newsletterDiscount(double amount) {
        if (amount > 100) {
            return amount * 0.10;
        }
        if (amount > 50) {
            return amount * 0.05;
        }
        return 0;
    }

    public double bestOf(double amount) {
        return Math.max(loyaltyDiscount(amount), Math.max(studentDiscount(amount), newsletterDiscount(amount)));
    }
}
