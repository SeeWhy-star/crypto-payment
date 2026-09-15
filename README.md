# Crypto Payment Demo

基于 Spring Boot 的区块链加密货币支付系统学习 Demo。

当前阶段（第 0 阶段）只包含可启动的 Spring Boot 应用和健康检查端点，后续按迭代计划逐步加入 PaymentIntent、Mock 区块链、链上校验和 Webhook。

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
