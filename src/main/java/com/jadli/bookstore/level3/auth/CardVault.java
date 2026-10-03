package com.jadli.bookstore.level3.auth;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import org.springframework.stereotype.Component;

@Component
public class CardVault {

    private static final byte[] KEY_BYTES = "12345678".getBytes(StandardCharsets.UTF_8);

    public String protect(String cardNumber) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("DES");
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, factory.generateSecret(new DESKeySpec(KEY_BYTES)));
            return Base64.getEncoder().encodeToString(cipher.doFinal(cardNumber.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("cannot protect card number", e);
        }
    }
}
