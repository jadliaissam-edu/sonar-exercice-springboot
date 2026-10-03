package com.jadli.bookstore.repository;

import com.jadli.bookstore.domain.Book;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByTitleContainingIgnoreCase(String fragment);

    List<Book> findByGenre(String genre);

    Optional<Book> findByIsbn(String isbn);
}
