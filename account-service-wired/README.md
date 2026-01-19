# account-service (wired MVP)

## What it does
- Auth: POST /auth/validate (header X-API-Key) -> {accountId}
- Risk: consumes Kafka topic risk.commands (RiskCheckCommand)
- Publishes Kafka topic risk.events (RiskApproved / RiskRejected)
- MVP risk rules:
  - Limit-only, cash-only
  - BUY requires available >= qty * limitPrice
  - SELL rejected (MVP)

## Run
mvn spring-boot:run

## Seed demo account (psql)
-- Connect as postgres user to db trading
CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO account.api_keys (api_key_hash, account_id, status)
VALUES (encode(digest('demo-key','sha256'),'hex'), 'acct-1', 'ACTIVE');

INSERT INTO account.cash_balances (account_id, currency, available, reserved)
VALUES ('acct-1', 'USD', 100000, 0);

## Test auth
curl -X POST http://localhost:8083/auth/validate -H "X-API-Key: demo-key"
