package ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.sync.methods;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import ru.eddyz.telegrambot.clients.tonapi.ton.exception.TONAPIError;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.blockchain.BlockchainAccountInspect;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.blockchain.BlockchainBlock;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.blockchain.BlockchainBlockShards;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.blockchain.BlockchainBlocks;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.blockchain.BlockchainConfig;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.blockchain.BlockchainRawAccount;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.blockchain.MethodExecutionResult;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.blockchain.RawBlockchainConfig;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.blockchain.ReducedBlocks;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.blockchain.Validators;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.transactions.Transaction;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.transactions.Transactions;
import ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.sync.TonapiClientBase;

public class BlockchainMethod extends TonapiClientBase {

  public BlockchainMethod(TonapiClientBase client) {
    super(client);
  }

  /**
   * Get reduced blockchain blocks data.
   *
   * @param from Starting block number
   * @param to   Ending block number
   * @return ReducedBlocks object containing the reduced blocks data
   * @throws TONAPIError if the request fails
   */
  public ReducedBlocks getReducedBlocks(long from, long to) throws TONAPIError {
    String method = "v2/blockchain/reduced/blocks";
    Map<String, Object> params = new HashMap<>();
    params.put("from", from);
    params.put("to", to);
    return this.get(method, params, null, new TypeReference<ReducedBlocks>() {
    });
  }

  /**
   * Get block data.
   *
   * @param blockId Block ID (string), example: "(-1,8000000000000000,4234234)"
   * @return BlockchainBlock object containing the block data
   * @throws TONAPIError if the request fails
   */
  public BlockchainBlock getBlockData(String blockId) throws TONAPIError {
    String method = String.format("v2/blockchain/blocks/%s", blockId);
    return this.get(method, null, null, new TypeReference<BlockchainBlock>() {
    });
  }

  /**
   * Get blockchain block shards.
   *
   * @param masterchainSeqno Masterchain block seqno
   * @return BlockchainBlockShards object containing the block shards
   * @throws TONAPIError if the request fails
   */
  public BlockchainBlockShards getBlock(long masterchainSeqno) throws TONAPIError {
    String method = String.format("v2/blockchain/masterchain/%d/shards", masterchainSeqno);
    return this.get(method, null, null, new TypeReference<BlockchainBlockShards>() {
    });
  }

  /**
   * Get all blocks in all shards and workchains between target and previous masterchain block
   * according to shards last blocks snapshot in masterchain. We don't recommend building your app
   * around this method because it has scalability issues and will work very slowly in the future.
   *
   * @param masterchainSeqno Masterchain block seqno
   * @return BlockchainBlocks object containing the blocks
   * @throws TONAPIError if the request fails
   */
  public BlockchainBlocks getBlocks(long masterchainSeqno) throws TONAPIError {
    String method = String.format("v2/blockchain/masterchain/%d/blocks", masterchainSeqno);
    return this.get(method, null, null, new TypeReference<BlockchainBlocks>() {
    });
  }

  /**
   * Get all transactions in all shards and workchains between target and previous masterchain block
   * according to shards last blocks snapshot in masterchain. We don't recommend building your app
   * around this method because it has scalability issues and will work very slowly in the future.
   *
   * @param masterchainSeqno Masterchain block seqno
   * @return Transactions object containing the transactions
   * @throws TONAPIError if the request fails
   */
  public Transactions getTransactionsShards(long masterchainSeqno) throws TONAPIError {
    String method = String.format("v2/blockchain/masterchain/%d/transactions", masterchainSeqno);
    return this.get(method, null, null, new TypeReference<Transactions>() {
    });
  }

  /**
   * Get blockchain config from a specific block, if present.
   *
   * @param masterchainSeqno Masterchain block seqno
   * @return BlockchainConfig object containing the blockchain config
   * @throws TONAPIError if the request fails
   */
  public BlockchainConfig getBlockchainConfig(long masterchainSeqno) throws TONAPIError {
    String method = String.format("v2/blockchain/masterchain/%d/config", masterchainSeqno);
    return this.get(method, null, null, new TypeReference<BlockchainConfig>() {
    });
  }

  /**
   * Get raw blockchain config from a specific block, if present.
   *
   * @param masterchainSeqno Masterchain block seqno
   * @return RawBlockchainConfig object containing the raw blockchain config
   * @throws TONAPIError if the request fails
   */
  public RawBlockchainConfig getRawBlockchainConfig(long masterchainSeqno) throws TONAPIError {
    String method = String.format("v2/blockchain/masterchain/%d/config/raw", masterchainSeqno);
    return this.get(method, null, null, new TypeReference<RawBlockchainConfig>() {
    });
  }

  /**
   * Get transactions from a block.
   *
   * @param blockId Block ID (string), example: "(-1,8000000000000000,4234234)"
   * @return Transactions object containing the transactions from the block
   * @throws TONAPIError if the request fails
   */
  public Transactions getTransactionFromBlock(String blockId) throws TONAPIError {
    String method = String.format("v2/blockchain/blocks/%s/transactions", blockId);
    return this.get(method, null, null, new TypeReference<Transactions>() {
    });
  }

  /**
   * Get transaction data.
   *
   * @param transactionId Transaction ID (string), example:
   *                      "22e8d62af53447cdedb333bf90040b9289ae90ea21ceac33999c9dbff9c5b1fc"
   * @return Transaction object containing the transaction data
   * @throws TONAPIError if the request fails
   */
  public Transaction getTransactionData(String transactionId) throws TONAPIError {
    String method = String.format("v2/blockchain/transactions/%s", transactionId);
    return this.get(method, null, null, new TypeReference<Transaction>() {
    });
  }

  /**
   * Get transaction data by message hash.
   *
   * @param msgId Message ID
   * @return Transaction object containing the transaction data
   * @throws TONAPIError if the request fails
   */
  public Transaction getTransactionByMessage(String msgId) throws TONAPIError {
    String method = String.format("v2/blockchain/messages/%s/transaction", msgId);
    return this.get(method, null, null, new TypeReference<Transaction>() {
    });
  }

  /**
   * Get blockchain validators.
   *
   * @return Validators object containing the validators information
   * @throws TONAPIError if the request fails
   */
  public Validators getValidators() throws TONAPIError {
    String method = "v2/blockchain/validators";
    return this.get(method, null, null, new TypeReference<Validators>() {
    });
  }

  /**
   * Get the last known masterchain block.
   *
   * @return BlockchainBlock object containing the last masterchain block
   * @throws TONAPIError if the request fails
   */
  public BlockchainBlock getLastMasterchainBlock() throws TONAPIError {
    String method = "v2/blockchain/masterchain-head";
    return this.get(method, null, null, new TypeReference<BlockchainBlock>() {
    });
  }

  /**
   * Get low-level information about an account taken directly from the blockchain.
   *
   * @param accountId Account ID
   * @return BlockchainRawAccount object containing the raw account information
   * @throws TONAPIError if the request fails
   */
  public BlockchainRawAccount getAccountInfo(String accountId) throws TONAPIError {
    String method = String.format("v2/blockchain/accounts/%s", accountId);
    return this.get(method, null, null, new TypeReference<BlockchainRawAccount>() {
    });
  }

  /**
   * Get account transactions.
   *
   * @param accountId Account ID
   * @param afterLt   Optional parameter to get transactions after specified logical time (lt)
   * @param beforeLt  Optional parameter to get transactions before specified logical time (lt)
   * @param limit     Number of records to return, default is 100
   * @return Transactions object containing the transactions
   * @throws TONAPIError if the request fails
   */
  public Transactions getAccountTransactions(
      String accountId,
      Long afterLt,
      Long beforeLt,
      int limit) throws TONAPIError {
    String method = String.format("v2/blockchain/accounts/%s/transactions", accountId);
    Map<String, Object> params = new HashMap<>();
    params.put("limit", limit);
    if (beforeLt != null) {
      params.put("before_lt", beforeLt);
    }
    if (afterLt != null) {
      params.put("after_lt", afterLt);
    }
    return this.get(method, params, null, new TypeReference<Transactions>() {
    });
  }

  /**
   * Execute get method for an account.
   *
   * @param accountId  Account ID
   * @param methodName Contract get method name
   * @param args       Optional list of arguments for the method
   * @return MethodExecutionResult object containing the result of the method execution
   * @throws TONAPIError if the request fails
   */
  public MethodExecutionResult executeGetMethod(
      String accountId,
      String methodName,
      String... args) throws TONAPIError {
    StringBuilder methodBuilder = new StringBuilder();
    methodBuilder.append(
        String.format("v2/blockchain/accounts/%s/methods/%s", accountId, methodName));
    if (args != null && args.length > 0) {
      StringJoiner joiner = new StringJoiner("&", "?", "");
      for (String arg : args) {
        joiner.add("args=" + arg);
      }
      methodBuilder.append(joiner);
    }
    String method = methodBuilder.toString();
    return this.get(method, null, null, new TypeReference<MethodExecutionResult>() {
    });
  }

  /**
   * Send message to the blockchain.
   *
   * @param boc  the base64 serialized bag-of-cells Example ->
   *             te6ccgECBQEAARUAAkWIAWTtae+KgtbrX26Bep8JSq8lFLfGOoyGR/xwdjfvpvEaHg
   * @param bocs a batch of bocs serialized in base64/hex are accepted
   * @return String if the message was sent successfully
   * @throws TONAPIError if the request fails
   */
  public String sendMessage(String boc, List<String> bocs) throws TONAPIError {
    String method = "v2/blockchain/message";

    Map<String, Object> body = new HashMap<>();
    body.put("boc", boc);
    body.put("batch", bocs);

    return this.post(method, null, body, null, new TypeReference<String>() {
    });
  }

  /**
   * Get blockchain config.
   *
   * @return BlockchainConfig object containing the blockchain config
   * @throws TONAPIError if the request fails
   */
  public BlockchainConfig getConfig() throws TONAPIError {
    String method = "v2/blockchain/config";
    return this.get(method, null, null, new TypeReference<BlockchainConfig>() {
    });
  }

  /**
   * Get raw blockchain config.
   *
   * @return RawBlockchainConfig object containing the raw blockchain config
   * @throws TONAPIError if the request fails
   */
  public RawBlockchainConfig getRawConfig() throws TONAPIError {
    String method = "v2/blockchain/config/raw";
    return this.get(method, null, null, new TypeReference<RawBlockchainConfig>() {
    });
  }

  /**
   * Inspect a blockchain account.
   *
   * @param accountId Account ID
   * @return BlockchainAccountInspect object containing the account inspection data
   * @throws TONAPIError if the request fails
   */
  public BlockchainAccountInspect inspectAccount(String accountId) throws TONAPIError {
    String method = String.format("v2/blockchain/accounts/%s/inspect", accountId);
    return this.get(method, null, null, new TypeReference<BlockchainAccountInspect>() {
    });
  }
}
