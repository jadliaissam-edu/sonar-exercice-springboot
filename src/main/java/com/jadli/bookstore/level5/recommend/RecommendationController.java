package com.jadli.bookstore.level5.recommend;

import com.jadli.bookstore.domain.Book;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService service;

    public RecommendationController(RecommendationService service) {
        this.service = service;
    }

    @GetMapping("/featured")
    public List<Book> featured(@RequestParam(defaultValue = "3") int count) {
        return service.featured(count);
    }

    @GetMapping("/etag")
    public String etag(@RequestParam String cover) {
        return service.coverEtag(cover);
    }
}
