package com.acme.trade.order;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class OrderRepository {
  private final JdbcTemplate jdbc;

  public OrderRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public String create(OrderSubmitCommand c) {
    String orderId = UUID.randomUUID().toString();
    jdbc.update(
      "INSERT INTO trade.orders (order_id, account_id, client_order_id, symbol, side, qty, limit_price, status) VALUES (?,?,?,?,?,?,?,?)",
      orderId, c.accountId(), c.clientOrderId(), c.symbol(), c.side(), c.qty(), c.limitPrice(), "PENDING_RISK"
    );
    return orderId;
  }
}
