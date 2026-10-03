package com.jadli.bookstore.level2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jadli.bookstore.domain.Book;
import com.jadli.bookstore.repository.BookRepository;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class StoreFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderExportService exportService;

    @Autowired
    private BookRepository bookRepository;

    @Test
    void listsSeededUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("alice"));
    }

    @Test
    void displayNameEndpoint() throws Exception {
        mockMvc.perform(get("/api/users/2/display-name"))
                .andExpect(status().isOk())
                .andExpect(content().string("BOB"));
    }

    @Test
    void unknownUserNameIsHandled() throws Exception {
        mockMvc.perform(get("/api/users/by-name/nobody"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value("resource not found"));
    }

    @Test
    void seededReviewsAreAveraged() throws Exception {
        mockMvc.perform(get("/api/reviews/books/1/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.average").value(4.5))
                .andExpect(jsonPath("$.count").value(2));
    }

    @Test
    void orderIsPlacedAndStockReduced() throws Exception {
        Book book = bookRepository.save(new Book("Stock Test", "Nobody", "0000000000001", 12.5, 10, "test"));
        String body = "{\"userId\":1,\"lines\":[{\"bookId\":" + book.getId() + ",\"quantity\":2}]}";
        mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(25.0));
        assertEquals(8, bookRepository.findById(book.getId()).orElseThrow().getStock());
    }

    @Test
    void orderWithTooLargeQuantityIsRejected() throws Exception {
        Book book = bookRepository.save(new Book("Rare Book", "Nobody", "0000000000002", 99.0, 1, "test"));
        String body = "{\"userId\":1,\"lines\":[{\"bookId\":" + book.getId() + ",\"quantity\":5}]}";
        mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void ordersAreExported(@TempDir Path dir) throws Exception {
        Path target = dir.resolve("orders.csv");
        int count = exportService.exportOrders(target);
        assertTrue(count >= 0);
        assertTrue(Files.readString(target).startsWith("id,userId"));
    }
}
