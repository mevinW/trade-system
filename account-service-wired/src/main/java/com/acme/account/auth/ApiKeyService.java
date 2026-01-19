package com.acme.account.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ApiKeyService {
  private final JdbcTemplate jdbc;

  public ApiKeyService(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public String validateAndGetAccountId(String apiKeyPlaintext) {
    String hash = sha256Hex(apiKeyPlaintext);

    String accountId = jdbc.query(
        "SELECT account_id FROM account.api_keys WHERE api_key_hash = ? AND status = 'ACTIVE'",
        ps -> ps.setString(1, hash),
        rs -> rs.next() ? rs.getString(1) : null
    );

    if (accountId == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or inactive API key");
    }
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
}
