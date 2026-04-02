package com.example.auth_service.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@Service
public class TokenService {
  private final String secret;
  private final long expirationSeconds;

  public TokenService(
    @Value("${security.token.secret}") String secret,
    @Value("${security.token.expiration-seconds}") long expirationSeconds) {
    this.secret = secret;
    this.expirationSeconds = expirationSeconds;
  }

  public String generateToken(UserDetails userDetails) {
    long expiresAt = Instant.now().getEpochSecond() + expirationSeconds;
    String payload = userDetails.getUsername() + ":" + expiresAt;
    String signature = hmacSha256(payload, secret);

    String rawToken = payload + ":" + signature;
    return Base64.getUrlEncoder().withoutPadding()
      .encodeToString(rawToken.getBytes(StandardCharsets.UTF_8));
  }

  private String hmacSha256(String value, String secret) {
    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      SecretKeySpec key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
      mac.init(key);
      byte[] hash = mac.doFinal(value.getBytes(StandardCharsets.UTF_8));
      return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    } catch (Exception e) {
      throw new IllegalStateException("Erreur de signature du token", e);
    }
  }

}
