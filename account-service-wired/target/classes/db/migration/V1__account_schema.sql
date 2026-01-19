CREATE TABLE IF NOT EXISTS account.api_keys (
  api_key_hash TEXT PRIMARY KEY,
  account_id   TEXT NOT NULL,
  status       TEXT NOT NULL,
  created_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS account.cash_balances (
  account_id TEXT PRIMARY KEY,
  currency   TEXT NOT NULL DEFAULT 'USD',
  available  NUMERIC(20,2) NOT NULL,
  reserved   NUMERIC(20,2) NOT NULL
);
