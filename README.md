# Crypto Payment Demo

基于 Spring Boot 的区块链加密货币支付系统学习 Demo。

当前阶段（第 2 阶段）实现 Mock 区块链支付：配置资产和网络，模拟链上交易，并刷新支付状态。数据仍使用内存存储。

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

## Mock 区块链支付

为订单配置加密货币支付：

```bash
curl -X POST http://localhost:8080/api/payment-intents/{paymentNo}/crypto-payment \
  -H "Content-Type: application/json" \
  -d '{"asset":"USDT","network":"ETHEREUM_SEPOLIA","depositAddress":"0xMerchant","expectedAmount":"25.00"}'
```

模拟一笔已确认的链上交易：

```bash
curl -X POST http://localhost:8080/api/mock/blockchain/transactions \
  -H "Content-Type: application/json" \
  -d '{"network":"ETHEREUM_SEPOLIA","transactionHash":"0xtest","asset":"USDT","fromAddress":"0xCustomer","toAddress":"0xMerchant","amount":"25.00","confirmed":true}'
```

刷新支付状态：

```bash
curl -X POST http://localhost:8080/api/payment-intents/{paymentNo}/refresh
```

刷新会校验网络、资产、收款地址和金额，匹配后才会将支付标记为 `SUCCEEDED`。Mock 网关通过接口抽象，后续可以替换为 web3j 测试网实现。
