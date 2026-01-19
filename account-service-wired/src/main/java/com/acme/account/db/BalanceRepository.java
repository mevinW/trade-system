package com.acme.account.db;

import java.math.BigDecimal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class BalanceRepository {
  private final JdbcTemplate jdbc;

  public BalanceRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  @Transactional
  public boolean reserveIfEnough(String accountId, BigDecimal amount) {
    int updated = jdbc.update(
        "UPDATE account.cash_balances SET available = available - ?, reserved = reserved + ? WHERE account_id = ? AND available >= ?",
        amount, amount, accountId, amount
    );
    return updated == 1;
  }
}
