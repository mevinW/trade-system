# infra (local dev)

## Start Kafka + Postgres
docker compose up -d

docker compose down
## Kafka UI
http://localhost:8088

## Postgres connection (admin)
host=localhost port=5432 db=trading user=postgres password=postgres

## Service DB users (created by init-db.sql)
account_svc / account_pw  (schema: account)
trade_svc   / trade_pw    (schema: trade)
