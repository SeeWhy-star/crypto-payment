package com.example.cryptopayment.infrastructure.blockchain;

import com.example.cryptopayment.domain.enums.CryptoAsset;
import com.example.cryptopayment.domain.enums.CryptoNetwork;
import com.example.cryptopayment.domain.model.CryptoTransaction;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

import static org.web3j.utils.Convert.Unit.ETHER;
import static org.web3j.utils.Convert.fromWei;

@Component
@Profile("web3j")
public class Web3jBlockchainGateway implements BlockchainGateway {
    private final Web3j web3j;

    public Web3jBlockchainGateway(Web3j web3j) {
        this.web3j = web3j;
    }

    @Override
    public Optional<CryptoTransaction> findTransaction(CryptoNetwork network, String transactionHash) {
        try {
            EthTransaction response = web3j.ethGetTransactionByHash(transactionHash).send();
            Optional<Transaction> transactionOptional = response.getTransaction();
            if (transactionOptional.isEmpty()) {
                return Optional.empty();
            }
            Transaction transaction = transactionOptional.get();
            EthGetTransactionReceipt receiptResponse = web3j.ethGetTransactionReceipt(transactionHash).send();
            Optional<TransactionReceipt> receiptOptional = receiptResponse.getTransactionReceipt();
            if (receiptOptional.isEmpty() || transaction.getBlockNumber() == null) {
                return Optional.empty();
            }
            TransactionReceipt receipt = receiptOptional.get();
            BigInteger blockNumber = transaction.getBlockNumber();
            BigInteger latestBlock = web3j.ethBlockNumber().send().getBlockNumber();
            int confirmations = latestBlock.subtract(blockNumber).add(BigInteger.ONE).intValueExact();
            BigDecimal amount = fromWei(transaction.getValue().toString(), ETHER);
            boolean successful = "0x1".equalsIgnoreCase(receipt.getStatus())
                    || "1".equals(receipt.getStatus());
            return Optional.of(new CryptoTransaction(network, transactionHash, CryptoAsset.ETH,
                    transaction.getFrom(), transaction.getTo(), amount, null, blockNumber.longValue(),
                    confirmations, successful));
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to query blockchain transaction", exception);
        }
    }

    @Override
    public Optional<CryptoTransaction> findMatchingTransaction(CryptoNetwork network, CryptoAsset asset,
                                                                 String tokenContract, String toAddress,
                                                                 BigDecimal amount) {
        return Optional.empty();
    }

    @Override
    public void recordTransaction(CryptoTransaction transaction) {
        throw new UnsupportedOperationException("Transactions cannot be recorded on a real blockchain gateway");
    }
}
