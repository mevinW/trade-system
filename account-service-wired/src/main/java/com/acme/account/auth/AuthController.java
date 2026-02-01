package com.acme.account.auth;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
  private final ApiKeyService apiKeyService;


  public AuthController(ApiKeyService apiKeyService) {
    this.apiKeyService = apiKeyService;
  }

  @PostMapping("/validate")
  public ValidateResponse validate(@RequestHeader("X-API-Key") String apiKey) {
    String accountId = apiKeyService.validateAndGetAccountId(apiKey);
    return new ValidateResponse(accountId);
  }

  public record ValidateResponse(String accountId) {}
}
