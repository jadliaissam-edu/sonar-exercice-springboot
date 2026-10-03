package com.jadli.bookstore.level1;

public class IsbnUtil {

    public static boolean isValidIsbn13(String isbn) {
        if (isbn == null || isbn.length() != 13) {
            return false;
        }
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = Character.getNumericValue(isbn.charAt(i));
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        int check = (10 - (sum % 10)) % 10;
        if (check == Character.getNumericValue(isbn.charAt(12))) {
            return true;
        } else {
            return false;
        }
    }

    public static String normalize(String isbn) {
        return isbn == null ? "" : isbn.replace("-", "").trim();
    }
}
