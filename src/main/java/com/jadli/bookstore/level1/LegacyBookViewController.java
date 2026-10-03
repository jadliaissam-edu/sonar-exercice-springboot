package com.jadli.bookstore.level1;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/legacy")
public class LegacyBookViewController {

    private final BookCatalogService catalogService;

    public LegacyBookViewController(BookCatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/books/{id}/summary")
    @ResponseBody
    public String summary(@PathVariable Long id) {
        var book = catalogService.findById(id);
        return book.getTitle() + " (" + book.getIsbn() + ")";
    }

    @GetMapping("/about")
    @ResponseBody
    public String about() {
        return "Online bookstore catalog";
    }
}
