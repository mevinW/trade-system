# gateway-service (wired MVP)

## Run
mvn spring-boot:run

## Submit order
POST http://localhost:8080/orders
Header: X-API-Key: <your_api_key>
Body:
{
  "clientOrderId": "c1",
  "symbol": "AAPL",
  "side": "BUY",
  "qty": 10,
  "limitPrice": 100.00
}

Publishes to Kafka: order.commands (key=accountId)
