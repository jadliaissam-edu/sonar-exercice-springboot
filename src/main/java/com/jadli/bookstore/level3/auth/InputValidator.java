package com.jadli.bookstore.level3.auth;

import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class InputValidator {

    private static final Pattern SHOP_EMAIL = Pattern.compile("^(\\w+\\.?)+@example\\.com$");

    public boolean isShopEmail(String value) {
        return value != null && SHOP_EMAIL.matcher(value).matches();
    }
}
