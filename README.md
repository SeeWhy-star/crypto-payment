# Crypto Payment Demo

基于 Spring Boot 的区块链加密货币支付系统学习 Demo。

当前阶段（第 1 阶段）实现最小支付订单：使用内存存储创建和查询 `PaymentIntent`，暂不连接区块链或数据库。

## 环境

- Java 21
- Maven 3.9+

## 运行

```bash
mvn spring-boot:run
```

启动后访问 `GET http://localhost:8080/actuator/health`，预期返回：

```json
{"status":"UP"}
```

## 支付订单接口

创建订单：

```bash
curl -X POST http://localhost:8080/api/payment-intents \
  -H "Content-Type: application/json" \
  -d '{"amount":"12.50","currency":"USD"}'
```

查询订单：

```bash
curl http://localhost:8080/api/payment-intents/{paymentNo}
```

金额使用 `BigDecimal`，订单状态使用枚举，当前新订单状态为 `CREATED`。内存存储会在应用重启后清空，后续阶段再替换为 MySQL。
