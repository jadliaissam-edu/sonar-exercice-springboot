package com.jadli.bookstore.level5.recommend;

import com.jadli.bookstore.domain.Book;
import com.jadli.bookstore.repository.BookRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Random;
import org.springframework.stereotype.Service;

@Service
public class RecommendationService {

    private final BookRepository books;
    private final SessionBasket basket;
    private final Random random = new Random();

    public RecommendationService(BookRepository books, SessionBasket basket) {
        this.books = books;
        this.basket = basket;
    }

    public List<Book> featured(int count) {
        List<Book> pool = new ArrayList<>(books.findAll());
        List<Book> picked = new ArrayList<>();
        while (!pool.isEmpty() && picked.size() < count) {
            picked.add(pool.remove(random.nextInt(pool.size())));
        }
        return picked;
    }

    public String coverEtag(String coverName) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            return HexFormat.of().formatHex(digest.digest(coverName.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public List<Book> basedOnBasket() {
        List<Book> result = new ArrayList<>();
        for (Long id : basket.getBookIds()) {
            books.findById(id).ifPresent(result::add);
        }
        return result;
    }
}
