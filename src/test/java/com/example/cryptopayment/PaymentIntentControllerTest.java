package com.example.cryptopayment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentIntentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateAndQueryPaymentIntent() throws Exception {
        String response = mockMvc.perform(post("/api/payment-intents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":\"12.50\",\"currency\":\" usd \"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.paymentNo").value(org.hamcrest.Matchers.startsWith("pi_")))
                .andExpect(jsonPath("$.amount").value(12.50))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andReturn().getResponse().getContentAsString();

        String paymentNo = com.jayway.jsonpath.JsonPath.read(response, "$.paymentNo");
        mockMvc.perform(get("/api/payment-intents/{paymentNo}", paymentNo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentNo").value(paymentNo))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    void shouldReturnSamePaymentIntentForRepeatedIdempotencyKey() throws Exception {
        String key = "checkout-123";
        String first = mockMvc.perform(post("/api/payment-intents")
                        .header("Idempotency-Key", key)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":\"8.00\",\"currency\":\"USD\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String second = mockMvc.perform(post("/api/payment-intents")
                        .header("Idempotency-Key", key)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":\"99.00\",\"currency\":\"EUR\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String firstPaymentNo = com.jayway.jsonpath.JsonPath.read(first, "$.paymentNo");
        String secondPaymentNo = com.jayway.jsonpath.JsonPath.read(second, "$.paymentNo");
        org.junit.jupiter.api.Assertions.assertEquals(firstPaymentNo, secondPaymentNo);
    }

    @Test
    void shouldRejectInvalidAmount() throws Exception {
        mockMvc.perform(post("/api/payment-intents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":\"0\",\"currency\":\"USD\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCreateDeterministicWebhookSignature() throws Exception {
        String body = "{\"event\":\"payment.succeeded\"}";
        String first = mockMvc.perform(post("/api/webhooks/sign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"payload\":\"" + body.replace("\"", "\\\"")
                                + "\",\"secret\":\"local-secret\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String second = mockMvc.perform(post("/api/webhooks/sign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"payload\":\"" + body.replace("\"", "\\\"")
                                + "\",\"secret\":\"local-secret\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String firstSignature = com.jayway.jsonpath.JsonPath.read(first, "$.signature");
        String secondSignature = com.jayway.jsonpath.JsonPath.read(second, "$.signature");
        org.junit.jupiter.api.Assertions.assertEquals(firstSignature, secondSignature);
    }

    @Test
    void shouldReturnNotFoundForUnknownPaymentIntent() throws Exception {
        mockMvc.perform(get("/api/payment-intents/pi_unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("PAYMENT_INTENT_NOT_FOUND"));
    }

    @Test
    void shouldRefreshMockCryptoPaymentToSucceeded() throws Exception {
        String paymentResponse = mockMvc.perform(post("/api/payment-intents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":\"25.00\",\"currency\":\"USD\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String paymentNo = com.jayway.jsonpath.JsonPath.read(paymentResponse, "$.paymentNo");

        mockMvc.perform(post("/api/payment-intents/{paymentNo}/crypto-payment", paymentNo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"asset\":\"USDT\",\"network\":\"ETHEREUM_SEPOLIA\","
                                + "\"depositAddress\":\"0xMerchant\",\"expectedAmount\":\"25.00\","
                                + "\"tokenContract\":\"0xUsdt\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("WAITING_PAYMENT"));

        mockMvc.perform(post("/api/mock/blockchain/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"network\":\"ETHEREUM_SEPOLIA\",\"transactionHash\":\"0xtest\","
                                + "\"asset\":\"USDT\",\"fromAddress\":\"0xCustomer\","
                                + "\"toAddress\":\"0xMerchant\",\"amount\":\"25.00\","
                                + "\"tokenContract\":\"0xUsdt\",\"blockNumber\":100,"
                                + "\"confirmations\":2,\"receiptSuccessful\":true}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/payment-intents/{paymentNo}/refresh", paymentNo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionHash").value("0xtest"))
                .andExpect(jsonPath("$.status").value("SUCCEEDED"));

        mockMvc.perform(get("/api/payment-intents/{paymentNo}", paymentNo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCEEDED"));

        mockMvc.perform(post("/api/payment-intents/{paymentNo}/refresh", paymentNo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCEEDED"));

        mockMvc.perform(post("/api/payment-intents/{paymentNo}/refunds", paymentNo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":\"10.00\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCEEDED"));

        mockMvc.perform(post("/api/payment-intents/{paymentNo}/refunds", paymentNo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":\"20.00\"}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("INVALID_REFUND"));
    }
}
