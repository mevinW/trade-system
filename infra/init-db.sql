-- Service DB users
CREATE USER account_svc WITH PASSWORD 'account_pw';
CREATE USER trade_svc   WITH PASSWORD 'trade_pw';

-- Schemas
CREATE SCHEMA IF NOT EXISTS account AUTHORIZATION account_svc;
CREATE SCHEMA IF NOT EXISTS trade   AUTHORIZATION trade_svc;

-- Minimal permissions
GRANT USAGE ON SCHEMA account TO account_svc;
GRANT USAGE ON SCHEMA trade   TO trade_svc;

-- Each service creates its own tables via Flyway.
