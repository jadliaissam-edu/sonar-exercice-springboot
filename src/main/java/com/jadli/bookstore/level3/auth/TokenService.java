package com.jadli.bookstore.level3.auth;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Random;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private static final String JWT_SECRET = "9f8Zk2LqP7xVb3Nw0TeYh6RmCs1UdJ4a";

    private final Random random = new Random();

    public String issueToken(String username) {
        String header = encode("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
        String payload = encode("{\"sub\":\"" + username + "\",\"iat\":" + System.currentTimeMillis() / 1000 + "}");
        return header + "." + payload + "." + sign(header + "." + payload);
    }

    public boolean verify(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return false;
        }
        return sign(parts[0] + "." + parts[1]).equals(parts[2]);
    }

    public String newResetToken() {
        return Long.toHexString(random.nextLong()) + Long.toHexString(random.nextLong());
    }

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(JWT_SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("cannot sign token", e);
        }
    }

    private String encode(String json) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(json.getBytes(StandardCharsets.UTF_8));
    }
}
