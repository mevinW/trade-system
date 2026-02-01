package com.acme.account.auth;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ApiKeyService {
  private final JdbcTemplate jdbc;
  private final Map<String, CacheEntry> validatedKeys = new ConcurrentHashMap<>();
  private static final long CACHE_TTL_MILLIS = 600_000; // 1 minute TTL for cache entries

  public ApiKeyService(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public String validateAndGetAccountId(String apiKeyPlaintext) {
    String hash = sha256Hex(apiKeyPlaintext);

    CacheEntry cachedAccountId = validatedKeys.get(hash);
    if (cachedAccountId != null) {
      if (cachedAccountId.isExpired()) {
        validatedKeys.remove(hash);
      } else {
        //System.out.println("AccountService found cached key" + apiKeyPlaintext);
        return cachedAccountId.accountId;
      }
    }

    String accountId = jdbc.query(
        "SELECT account_id FROM account.api_keys WHERE api_key_hash = ? AND status = 'ACTIVE'",
        ps -> ps.setString(1, hash),
        rs -> rs.next() ? rs.getString(1) : null
    );

    if (accountId == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or inactive API key");
    }

    validatedKeys.put(hash, new CacheEntry(accountId, CACHE_TTL_MILLIS));
    return accountId;
  }

  public static String sha256Hex(String s) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] dig = md.digest(s.getBytes(StandardCharsets.UTF_8));
      StringBuilder sb = new StringBuilder();
      for (byte b : dig) sb.append(String.format("%02x", b));
      return sb.toString();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static class CacheEntry {
    private final String accountId;
    private final long expiresAtMillis;

    CacheEntry(String accountId, long ttlMillis) {
      this.accountId = accountId;
      this.expiresAtMillis = System.currentTimeMillis() + ttlMillis;
    }

    boolean isExpired() {
      return System.currentTimeMillis() > expiresAtMillis;
    }
  }
}
