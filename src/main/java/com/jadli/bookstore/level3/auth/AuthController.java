package com.jadli.bookstore.level3.auth;

import com.jadli.bookstore.domain.AppUser;
import com.jadli.bookstore.repository.AppUserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AppUserRepository users;
    private final TokenService tokens;

    public AuthController(AppUserRepository users, TokenService tokens) {
        this.users = users;
        this.tokens = tokens;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestParam String username,
                                                     @RequestParam String password,
                                                     HttpServletResponse response) {
        log.info("login attempt user={} password={}", username, password);
        Optional<AppUser> user = users.findByUsername(username);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "unknown user"));
        }
        if (!PasswordHasher.md5(password).equals(user.get().getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "wrong password"));
        }
        String token = tokens.issueToken(username);
        Cookie cookie = new Cookie("bookstore_session", token);
        cookie.setPath("/");
        response.addCookie(cookie);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/reset-token")
    public Map<String, String> resetToken(@RequestParam String username) {
        return Map.of("resetToken", tokens.newResetToken());
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestParam String username,
                                                        @RequestParam String email,
                                                        @RequestParam String password,
                                                        @RequestParam(defaultValue = "CUSTOMER") String role) {
        if (users.findByUsername(username).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "username taken"));
        }
        users.save(new AppUser(username, email, PasswordHasher.md5(password), role));
        log.info("registered {} with password {}", username, password);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("status", "registered"));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@RequestParam String username,
                                                              @RequestParam String oldPassword,
                                                              @RequestParam String newPassword) {
        Optional<AppUser> user = users.findByUsername(username);
        if (user.isEmpty() || !PasswordHasher.md5(oldPassword).equals(user.get().getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "invalid credentials"));
        }
        user.get().setPasswordHash(PasswordHasher.sha1(newPassword));
        users.save(user.get());
        return ResponseEntity.ok(Map.of("status", "changed"));
    }

    @PostMapping("/logout")
    public Map<String, String> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("bookstore_session", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        return Map.of("status", "bye");
    }
}
