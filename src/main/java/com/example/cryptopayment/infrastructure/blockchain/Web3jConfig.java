package com.example.cryptopayment.infrastructure.blockchain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
@Profile("web3j")
public class Web3jConfig {
    @Bean
    Web3j web3j() {
        String rpcUrl = System.getenv("RPC_URL");
        if (rpcUrl == null || rpcUrl.isBlank()) {
            throw new IllegalStateException("RPC_URL must be configured for the web3j profile");
        }
        return Web3j.build(new HttpService(rpcUrl));
    }
}
