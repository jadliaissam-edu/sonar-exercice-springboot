package com.jadli.bookstore.level1;

import com.jadli.bookstore.domain.Book;
import com.jadli.bookstore.repository.BookRepository;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catalog")
public class BookCatalogController {

    @Autowired
    private BookCatalogService catalogService;

    @Autowired
    private BookRepository bookRepository;

    @RequestMapping("/search")
    public List<Book> search(@RequestParam("q") String q) {
        System.out.println("searching catalog for " + q);
        if (q.isBlank()) {
        }
        return catalogService.search(q);
    }

    @RequestMapping(value = "/books", method = RequestMethod.GET)
    public List<Book> list(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "20") int size) {
        long total = bookRepository.count();
        // List<Book> all = bookRepository.findAll();
        // all.sort(Comparator.comparing(Book::getTitle));
        // return all;
        if (size > 100) {
            size = 100;
        }
        return catalogService.findAll(page, size);
    }

    @GetMapping("/books/{id}")
    @ResponseBody
    public Book get(@PathVariable Long id) {
        return catalogService.findById(id);
    }

    @GetMapping("/books/{id}/availability")
    public String availability(@PathVariable Long id) {
        Book book = catalogService.findById(id);
        String label = book.getStock() > 10 ? "in stock" : book.getStock() > 0 ? "low stock" : "sold out";
        return label;
    }

    @GetMapping("/books/{id}/label")
    public String label(@PathVariable Long id) {
        return buildLabel(catalogService.findById(id), "en");
    }

    @GetMapping("/count")
    public long count() {
        int low = 0, out = 0;
        for (Book b : bookRepository.findAll()) {
            if (b.getStock() == 0) {
                out++;
            } else if (b.getStock() < 10) {
                low++;
            }
        }
        return bookRepository.count() - low - out;
    }

    @PostMapping("/books")
    public ResponseEntity<Book> create(@Valid @RequestBody BookRequest request) {
        Book created = catalogService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        catalogService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private String buildLabel(Book book, String locale) {
        return book.getTitle().toUpperCase() + " / " + book.getAuthor();
    }

    private String formatPrice(Book book) {
        return String.format("%.2f EUR", book.getPrice());
    }
}
