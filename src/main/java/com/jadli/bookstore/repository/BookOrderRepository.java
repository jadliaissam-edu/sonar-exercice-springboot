package com.jadli.bookstore.repository;

import com.jadli.bookstore.domain.BookOrder;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookOrderRepository extends JpaRepository<BookOrder, Long> {

    List<BookOrder> findByUserId(Long userId);
}
