package com.jadli.bookstore.level3;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jadli.bookstore.level3.auth.TokenService;
import org.junit.jupiter.api.Test;

class TokenServiceTest {

    private final TokenService tokens = new TokenService();

    @Test
    void issuedTokenVerifies() {
        assertTrue(tokens.verify(tokens.issueToken("alice")));
    }

    @Test
    void resetTokensDiffer() {
        assertNotEquals(tokens.newResetToken(), tokens.newResetToken());
    }

}
