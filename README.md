# Crypto Payment Demo

基于 Spring Boot 的区块链加密货币支付系统学习 Demo。

当前阶段（第 4 阶段）支持 MySQL 持久化：默认仍使用内存存储，启用 `mysql` profile 后切换到 JPA/MySQL。

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

刷新会校验网络、资产、代币合约、收款地址、金额和 Receipt 成功状态；交易确认数达到最低要求（当前为 2）后才会将支付标记为 `SUCCEEDED`。Mock 网关通过接口抽象，后续可以替换为 web3j 测试网实现。

## MySQL

启动数据库：

```bash
docker compose up -d mysql
```

使用 MySQL profile 启动应用：

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

数据库账号密码通过 `DB_USERNAME`、`DB_PASSWORD` 环境变量配置。示例密码仅用于本地学习，请勿用于生产环境或提交真实密码。

## 幂等请求

创建订单时可携带 `Idempotency-Key` 请求头。相同 key 在 24 小时内重复提交会返回同一个订单，不会重复创建。默认使用内存实现；启用 `redis` profile 后使用 Redis，并通过 Redisson 分布式锁保护并发请求：

```bash
docker compose up -d redis
mvn spring-boot:run -Dspring-boot.run.profiles=redis
```

## web3j 测试网

启用 `web3j` profile 时，应用通过 `RPC_URL` 查询测试网原生 ETH 交易、Receipt 和确认数。RPC 地址必须通过环境变量提供：

```powershell
$env:RPC_URL = "https://your-sepolia-rpc.example"
mvn spring-boot:run "-Dspring-boot.run.profiles=mysql,redis,web3j"
```

当前 web3j 网关只读取链上数据，不发送交易；USDT 的 ERC-20 Transfer 日志解析将在后续迭代加入。不要配置主网 RPC，也不要把 RPC URL、API key 或钱包私钥提交到 Git。

## Webhook

`POST /api/webhooks/sign` 使用 HMAC-SHA256 为回调 payload 生成签名。生产环境中 secret 只能来自安全配置，不能放在请求日志或 Git 中。RabbitMQ 发布器通过 `rabbitmq` profile 启用：

```bash
docker compose up -d rabbitmq
mvn spring-boot:run -Dspring-boot.run.profiles=rabbitmq
```

配置 `webhook` profile 后，支付成功事件会向 `WEBHOOK_URL` 发起 HTTP POST；请求带有 `X-Webhook-Id`、`X-Webhook-Type` 和 `X-Webhook-Signature`，非 2xx 或网络异常最多重试 3 次。`WEBHOOK_SECRET` 仅从环境变量读取。
