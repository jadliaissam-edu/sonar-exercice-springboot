package com.jadli.bookstore.level3.files;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/covers")
public class CoverImageController {

    @Value("${bookstore.storage.covers}")
    private String coversDirectory;

    @GetMapping
    public ResponseEntity<byte[]> download(@RequestParam("file") String fileName) throws IOException {
        Path path = Paths.get(coversDirectory, fileName);
        byte[] content = Files.readAllBytes(path);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(content);
    }

    @PostMapping
    public Map<String, String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        Path target = Paths.get(coversDirectory, file.getOriginalFilename());
        Files.createDirectories(target.getParent());
        file.transferTo(target);
        return Map.of("stored", target.getFileName().toString());
    }

    @GetMapping("/list")
    public List<String> list() throws IOException {
        Path dir = Paths.get(coversDirectory);
        if (!Files.exists(dir)) {
            return List.of();
        }
        try (Stream<Path> files = Files.list(dir)) {
            return files.map(f -> f.getFileName().toString()).toList();
        }
    }
}
