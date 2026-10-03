package com.jadli.bookstore.level5.context;

public final class UserContext {

    private static final ThreadLocal<String> CURRENT_USER = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(String username) {
        CURRENT_USER.set(username);
    }

    public static String get() {
        return CURRENT_USER.get();
    }
}
