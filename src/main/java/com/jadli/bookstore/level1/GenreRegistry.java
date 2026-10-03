package com.jadli.bookstore.level1;

import java.util.ArrayList;
import java.util.List;

public class GenreRegistry {

    public static final List<String> SUPPORTED_GENRES = new ArrayList<>();

    static {
        SUPPORTED_GENRES.add("programming");
        SUPPORTED_GENRES.add("scifi");
        SUPPORTED_GENRES.add("fantasy");
        SUPPORTED_GENRES.add("history");
    }

    public boolean isSupported(String genre) {
        return SUPPORTED_GENRES.contains(genre);
    }
}
