# match-service (wired MVP)

## What it does
- Consumes `risk.events`
  - On `RiskApproved`, it:
    1) Updates `trade.orders.status` -> APPROVED
    2) Immediately fills the order (MVP): `trade.orders.status` -> FILLED
    3) Publishes `trade.events` with `OrderFilled`

## Run
mvn clean package
mvn spring-boot:run

## Validate
1) Submit order via gateway
2) Confirm:
   - Kafka UI: `trade.events` has `OrderFilled`
   - Postgres: `trade.orders.status` = FILLED for that order_id
