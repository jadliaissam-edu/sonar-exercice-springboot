package com.jadli.bookstore.level1;

import com.jadli.bookstore.domain.Book;
import com.jadli.bookstore.repository.BookRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BookCatalogService {

    @Autowired
    private BookRepository bookRepository;

    private String lastSearchTerm;

    private int cacheHits;

    // TODO add paging by genre once the front end supports it
    public List<Book> findAll(int page, int size) {
        List<Book> all = bookRepository.findAll();
        int from = Math.min(page * size, all.size());
        int to = Math.min(from + size, all.size());
        return new ArrayList<>(all.subList(from, to));
    }

    public ArrayList<Book> search(String term) {
        ArrayList<Book> result = new ArrayList<>();
        // FIXME search should also look at the author
        List<Book> found = bookRepository.findByTitleContainingIgnoreCase(term);
        if (found.size() == 0) {
            return result;
        }
        result.addAll(found);
        return result;
    }

    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found: " + id));
    }

    public Book create(BookRequest request) {
        if (request.getTitle() != null) {
            if (request.getTitle().length() > 200) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title too long");
            }
        }
        if (bookRepository.findByIsbn(request.getIsbn()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Book already exists: " + request.getIsbn());
        }
        Book book = new Book(request.getTitle(), request.getAuthor(), request.getIsbn(),
                request.getPrice(), request.getStock(), request.getGenre());
        Book saved = bookRepository.save(book);
        return saved;
    }

    public Book updateStock(Long id, int stock) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found: " + id));
        int previousStock = book.getStock();
        book.setStock(stock);
        // book.setUpdatedAt(LocalDateTime.now());
        // auditLog.record(id, previousStock, stock);
        return bookRepository.save(book);
    }

    public void delete(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found: " + id);
        }
        bookRepository.deleteById(id);
    }

    public Book findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found: " + isbn));
    }

    public void clearCache() {
    }
}
