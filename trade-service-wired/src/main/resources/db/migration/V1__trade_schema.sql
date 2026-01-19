CREATE TABLE IF NOT EXISTS trade.orders (
  order_id TEXT PRIMARY KEY,
  account_id TEXT NOT NULL,
  client_order_id TEXT NOT NULL,
  symbol TEXT NOT NULL,
  side TEXT NOT NULL,
  qty BIGINT NOT NULL,
  limit_price NUMERIC(20,2) NOT NULL,
  status TEXT NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
