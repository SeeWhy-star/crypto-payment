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
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Uint;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;
import java.util.Arrays;
import java.util.List;

import static org.web3j.utils.Convert.Unit.ETHER;
import static org.web3j.utils.Convert.fromWei;

@Component
@Profile("web3j")
public class Web3jBlockchainGateway implements BlockchainGateway {
    private static final int USDT_DECIMALS = 6;
    private static final Event TRANSFER_EVENT = new Event("Transfer", Arrays.asList(
            new TypeReference<Address>() {}, new TypeReference<Address>() {},
            new TypeReference<Uint256>() {}));
    private final Web3j web3j;

    public Web3jBlockchainGateway(Web3j web3j) {
        this.web3j = web3j;
    }

    @Override
    public Optional<CryptoTransaction> findTransaction(CryptoNetwork network, String transactionHash) {
        return findNativeTransaction(network, transactionHash);
    }

    @Override
    public Optional<CryptoTransaction> findTransaction(CryptoNetwork network, String transactionHash,
                                                       CryptoAsset asset, String tokenContract) {
        if (asset == CryptoAsset.USDT) {
            return findTokenTransaction(network, transactionHash, tokenContract);
        }
        return findNativeTransaction(network, transactionHash);
    }

    private Optional<CryptoTransaction> findNativeTransaction(CryptoNetwork network, String transactionHash) {
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

    private Optional<CryptoTransaction> findTokenTransaction(CryptoNetwork network, String transactionHash,
                                                               String tokenContract) {
        try {
            EthTransaction response = web3j.ethGetTransactionByHash(transactionHash).send();
            Optional<Transaction> transactionOptional = response.getTransaction();
            EthGetTransactionReceipt receiptResponse = web3j.ethGetTransactionReceipt(transactionHash).send();
            Optional<TransactionReceipt> receiptOptional = receiptResponse.getTransactionReceipt();
            if (transactionOptional.isEmpty() || receiptOptional.isEmpty()
                    || transactionOptional.get().getBlockNumber() == null) {
                return Optional.empty();
            }
            Transaction transaction = transactionOptional.get();
            TransactionReceipt receipt = receiptOptional.get();
            String transferSignature = EventEncoder.encode(TRANSFER_EVENT);
            Optional<CryptoTransaction> transfer = receipt.getLogs().stream()
                    .filter(log -> log.getAddress().equalsIgnoreCase(tokenContract))
                    .filter(log -> !log.getTopics().isEmpty() && log.getTopics().get(0).equalsIgnoreCase(transferSignature))
                    .map(log -> toTokenTransaction(network, transaction, receipt, log, tokenContract))
                    .findFirst();
            return transfer;
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to query ERC-20 transfer", exception);
        }
    }

    private CryptoTransaction toTokenTransaction(CryptoNetwork network, Transaction transaction,
                                                  TransactionReceipt receipt,
                                                  org.web3j.protocol.core.methods.response.Log log,
                                                  String tokenContract) {
        String from = FunctionReturnDecoder.decodeIndexedValue(log.getTopics().get(1),
                new TypeReference<Address>() {}).getValue().toString();
        String to = FunctionReturnDecoder.decodeIndexedValue(log.getTopics().get(2),
                new TypeReference<Address>() {}).getValue().toString();
        @SuppressWarnings("unchecked")
        List<Type> decoded = FunctionReturnDecoder.decode(log.getData(),
                (List) Arrays.asList(new TypeReference<Uint256>() {}));
        BigInteger rawAmount = ((Uint) decoded.get(0)).getValue();
        BigInteger blockNumber = transaction.getBlockNumber();
        int confirmations;
        try {
            confirmations = web3j.ethBlockNumber().send().getBlockNumber()
                    .subtract(blockNumber).add(BigInteger.ONE).intValueExact();
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to query latest block", exception);
        }
        boolean successful = "0x1".equalsIgnoreCase(receipt.getStatus()) || "1".equals(receipt.getStatus());
        return new CryptoTransaction(network, transaction.getHash(), CryptoAsset.USDT, from, to,
                new BigDecimal(rawAmount).movePointLeft(USDT_DECIMALS), tokenContract,
                blockNumber.longValue(), confirmations, successful);
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
