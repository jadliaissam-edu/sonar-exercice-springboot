package com.jadli.bookstore.level1;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jadli.bookstore.domain.Book;
import com.jadli.bookstore.repository.BookRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BookCatalogController.class)
@AutoConfigureMockMvc(addFilters = false)
public class BookCatalogControllerTest {

    @Autowired
    public MockMvc mockMvc;

    @MockBean
    private BookCatalogService catalogService;

    @MockBean
    private BookRepository bookRepository;

    @Test
    public void listReturnsBooks() throws Exception {
        when(catalogService.findAll(0, 20)).thenReturn(List.of(new Book("Dune", "Frank Herbert", "1", 9.99, 3, "scifi")));
        mockMvc.perform(get("/api/catalog/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Dune"));
    }

    @Test
    public void listCapsPageSize() throws Exception {
        when(catalogService.findAll(0, 100)).thenReturn(List.of());
        mockMvc.perform(get("/api/catalog/books?size=500"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    public void availabilityLabels() throws Exception {
        when(catalogService.findById(1L)).thenReturn(new Book("A", "B", "1", 1, 50, "x"));
        when(catalogService.findById(2L)).thenReturn(new Book("A", "B", "2", 1, 5, "x"));
        when(catalogService.findById(3L)).thenReturn(new Book("A", "B", "3", 1, 0, "x"));
        mockMvc.perform(get("/api/catalog/books/1/availability")).andExpect(content().string("in stock"));
        mockMvc.perform(get("/api/catalog/books/2/availability")).andExpect(content().string("low stock"));
        mockMvc.perform(get("/api/catalog/books/3/availability")).andExpect(content().string("sold out"));
    }

    @Test
    public void createValidBook() throws Exception {
        when(catalogService.create(any(BookRequest.class))).thenReturn(new Book("Dune", "Frank Herbert", "978", 9.99, 3, "scifi"));
        String body = "{\"title\":\"Dune\",\"author\":\"Frank Herbert\",\"isbn\":\"978\",\"price\":9.99,\"stock\":3}";
        mockMvc.perform(post("/api/catalog/books").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isbn").value("978"));
    }

    @Test
    public void createRejectsBlankTitle() throws Exception {
        String body = "{\"title\":\"\",\"isbn\":\"978\",\"price\":9.99,\"stock\":3}";
        mockMvc.perform(post("/api/catalog/books").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/catalog/books/4")).andExpect(status().isNoContent());
    }
}
