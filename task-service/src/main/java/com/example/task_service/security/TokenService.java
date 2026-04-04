package com.example.task_service.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@Service
public class TokenService {
  private final String secret;

  public TokenService(@Value("${security.token.secret}") String secret) {
    this.secret = secret;
  }

  public boolean isValid(String token) {
    try {
      String decoded = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
      String[] parts = decoded.split(":");
      if (parts.length != 3) return false;

      String username = parts[0];
      long expiresAt = Long.parseLong(parts[1]);
      String signature = parts[2];

      String payload = username + ":" + expiresAt;
      String expectedSignature = hmacSha256(payload, secret);

      return !username.isBlank()
        && expiresAt > Instant.now().getEpochSecond()
        && expectedSignature.equals(signature);
    } catch (Exception e) {
      return false;
    }
  }

  public String extractUsername(String token) {
    String decoded = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
    String[] parts = decoded.split(":");
    if (parts.length != 3) {
      throw new IllegalArgumentException("Token invalide");
    }
    return parts[0];
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
