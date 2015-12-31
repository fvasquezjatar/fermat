package com.bitdubai.fermat_bch_api.layer.crypto_network.bitcoin.interfaces;

import com.bitdubai.fermat_api.layer.all_definition.enums.BlockchainNetworkType;
import com.bitdubai.fermat_api.layer.all_definition.enums.VaultType;
import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.TransactionSender;
import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.crypto_transactions.CryptoStatus;
import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.crypto_transactions.CryptoTransaction;
import com.bitdubai.fermat_bch_api.layer.crypto_network.bitcoin.exceptions.CantBroadcastTransactionException;
import com.bitdubai.fermat_bch_api.layer.crypto_network.bitcoin.exceptions.CantFixTransactionInconsistenciesException;
import com.bitdubai.fermat_bch_api.layer.crypto_network.bitcoin.exceptions.CantGetCryptoTransactionException;
import com.bitdubai.fermat_bch_api.layer.crypto_network.bitcoin.exceptions.CantGetTransactionCryptoStatusException;
import com.bitdubai.fermat_bch_api.layer.crypto_network.bitcoin.exceptions.CantMonitorBitcoinNetworkException;
import com.bitdubai.fermat_bch_api.layer.crypto_vault.enums.CryptoVaults;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.UTXOProvider;

import java.util.List;
import java.util.UUID;

/**
 * Created by rodrigo on 9/30/15.
 */
public interface BitcoinNetworkManager extends TransactionSender<CryptoTransaction> {

    /**
     * Starts monitoring the network active networks with the list of keys passed.
     * @param keyList
     * @throws CantMonitorBitcoinNetworkException
     */
    void monitorNetworkFromKeyList(CryptoVaults vault, List<BlockchainNetworkType> blockchainNetworkTypes,List<ECKey> keyList) throws CantMonitorBitcoinNetworkException;

    /**
     * Gests all the CryptoTransactions that matchs this transaction Hash
     * @param txHash
     * @return
     * @throws CantGetCryptoTransactionException
     */
    List<CryptoTransaction> getCryptoTransaction(String txHash) throws CantGetCryptoTransactionException;


    /**
     * Will get the CryptoTransaction directly from the blockchain by requesting it to a peer.
     * If the transaction is not part of any of our vaults, we will ask it to a connected peer to retrieve it.
     * @param txHash the Hash of the transaction we are going to look for.
     * @param blockHash the Hash of block where this transaction was stored..
     * @return a CryptoTransaction with the information of the transaction.
     * @throws CantGetCryptoTransactionException
     */
    CryptoTransaction getCryptoTransactionFromBlockChain(String txHash, String blockHash) throws CantGetCryptoTransactionException;

    /**
     * Broadcast a well formed, commited and signed transaction into the specified network
     * @param blockchainNetworkType
     * @param tx
     * @param transactionId the internal Fermat Transaction
     * @throws CantBroadcastTransactionException
     */
    void broadcastTransaction(BlockchainNetworkType blockchainNetworkType, Transaction tx, UUID transactionId) throws CantBroadcastTransactionException;

    /**
     * Gets the UTXO provider from the CryptoNetwork on the specified Network
     * @param blockchainNetworkType
     * @return
     */
    UTXOProvider getUTXOProvider(BlockchainNetworkType blockchainNetworkType);


    /**
     * Get the bitcoin transaction stored by the CryptoNetwork
     * @param blockchainNetworkType the network type
     * @param transactionHash the transsaction hash
     * @return the bitcoin transaction
     */
    Transaction getBitcoinTransaction(BlockchainNetworkType blockchainNetworkType, String transactionHash);

    /**
     * Gets the bitcoin transactions stored by the CryptoNetwork
     * @param blockchainNetworkType     the network type
     * @param ecKey the ECKey that is affected by the transaction
     * @return the bitcoin transaction
     */
    List<Transaction> getBitcoinTransaction(BlockchainNetworkType blockchainNetworkType, ECKey ecKey);

    /**
     * Gets the bitcoin transactions stored by the CryptoNetwork
     * @param blockchainNetworkType the network type.
     * @param ecKeys the list of ECKeys affected by the transactions returned.
     * @return the bitcoin transaction
     */
    List<Transaction> getBitcoinTransaction(BlockchainNetworkType blockchainNetworkType, List<ECKey> ecKeys);

    /**
     * Get the bitcoin transaction stored by the CryptoNetwork
     * @param blockchainNetworkType the network type
     * @param vaultType the crypto vault that generated the keys that affects the returned transactions
     * @return the bitcoin transaction
     */
    List<Transaction> getBitcoinTransaction(BlockchainNetworkType blockchainNetworkType, VaultType vaultType);

    /**
     * Will get all the CryptoTransactions stored in the CryptoNetwork which are a child of a parent Transaction
     * @param parentHash
     * @return
     * @throws CantGetCryptoTransactionException
     */
    List<CryptoTransaction> getChildCryptoTransaction(String parentHash) throws CantGetCryptoTransactionException;

    /**
     * Will get all the CryptoTransactions stored in the CryptoNetwork which are a child of a parent Transaction
     * @param parentHash the parent transaction
     * @param depth the depth of how many transactions we will navigate until we reach the parent transaction. Max is 10
     * @return
     * @throws CantGetCryptoTransactionException
     */
    List<CryptoTransaction> getChildCryptoTransaction(String parentHash, int depth) throws CantGetCryptoTransactionException;


    /**
     * gets the current Crypto Status for the specified Transaction ID
     * @param txHash the Bitcoin transaction hash
     * @return the last crypto status
     * @throws CantGetTransactionCryptoStatusException
     */
    CryptoStatus getCryptoStatus(String txHash) throws CantGetTransactionCryptoStatusException;

    /**
     * Will check and fix any inconsistency that may be in out transaction table.
     * For example, If i don't have all adressTo or From, or coin values of zero.
     * @throws CantFixTransactionInconsistenciesException
     */
    void fixTransactionInconsistencies() throws CantFixTransactionInconsistenciesException;

}
