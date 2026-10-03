package com.jadli.bookstore.level2;

import com.jadli.bookstore.domain.AppUser;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserProfileController {

    private final UserProfileService service;

    public UserProfileController(UserProfileService service) {
        this.service = service;
    }

    @GetMapping
    public List<AppUser> all() {
        return service.listAll();
    }

    @PostMapping
    public AppUser register(@RequestBody AppUser user) {
        return service.register(user);
    }

    @GetMapping("/by-name/{username}")
    public AppUser byName(@PathVariable String username) {
        return service.getByUsername(username);
    }

    @GetMapping("/{id}/display-name")
    public String displayName(@PathVariable Long id) {
        return service.displayName(id);
    }

    @GetMapping("/ping")
    private String ping() {
        return "users ok";
    }

    @GetMapping("/by-email")
    public ResponseEntity<AppUser> byEmail(@RequestParam String email) {
        try {
            AppUser user = service.findByEmail(email).orElseThrow();
            return ResponseEntity.ok(user);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.OK).build();
        }
    }
}
