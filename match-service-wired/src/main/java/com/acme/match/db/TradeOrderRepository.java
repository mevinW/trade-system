package com.acme.match.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class TradeOrderRepository {
  private final JdbcTemplate jdbc;

  public TradeOrderRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  @Transactional
  public TradeOrderSnapshot approveAndLoad(String orderId) {
    jdbc.update("UPDATE trade.orders SET status = ? WHERE order_id = ?", "APPROVED", orderId);

    return jdbc.query(
        "SELECT account_id, symbol, side, qty, limit_price FROM trade.orders WHERE order_id = ?",
        ps -> ps.setString(1, orderId),
        rs -> {
          if (!rs.next()) return null;
          return new TradeOrderSnapshot(
              rs.getString("account_id"),
              rs.getString("symbol"),
              rs.getString("side"),
              rs.getLong("qty"),
              rs.getBigDecimal("limit_price")
          );
        }
    );
  }

  @Transactional
  public void markFilled(String orderId) {
    jdbc.update("UPDATE trade.orders SET status = ? WHERE order_id = ?", "FILLED", orderId);
  }

  public record TradeOrderSnapshot(String accountId, String symbol, String side, long qty, java.math.BigDecimal limitPrice) {}
}
