package com.acme.gateway.account;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AccountClient {
  private final RestClient client;

  public AccountClient(@Value("${account.baseUrl}") String baseUrl) {
    this.client = RestClient.builder().baseUrl(baseUrl).build();
  }

  public String validateApiKeyAndGetAccountId(String apiKey) {
    var resp = client.post()
        .uri("/auth/validate")
        .header("X-API-Key", apiKey)
        .retrieve()
        .body(ValidateResponse.class);

    if (resp == null || resp.accountId() == null || resp.accountId().isBlank()) {
      throw new IllegalStateException("account-service returned empty accountId");
    }
    return resp.accountId();
  }

  public record ValidateResponse(String accountId) {}
}
