package ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.async.methods;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.glassfish.grizzly.utils.Pair;
import ru.eddyz.telegrambot.clients.tonapi.ton.exception.TONAPIError;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.gasless.GaslessConfig;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.gasless.SignRawParams;
import ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.async.AsyncTonapiClientBase;

public class GaslessMethod extends AsyncTonapiClientBase {

  public GaslessMethod(AsyncTonapiClientBase client) {
    super(client);
  }

  /**
   * Returns configuration of gasless transfers.
   *
   * @return CompletableFuture of GaslessConfig object containing gasless transfer configuration
   * @throws TONAPIError if the request fails
   */
  public CompletableFuture<GaslessConfig> getConfig() throws TONAPIError {
    String method = "v2/gasless/config";
    return this.get(method, null, null, new TypeReference<GaslessConfig>() {
    });
  }

  /**
   * Returns estimated gas price.
   *
   * @param jettonMasterAddress Jetton Master Address.
   * @param walletAddress       Wallet address the tx is going to be made from
   * @param walletPublicKey     The public key of the wallet
   * @param messageList         List of messages in a form of BOC
   * @return CompletableFuture of SignRawParams object containing the estimated gas price
   * @throws TONAPIError if the request fails
   */
  public CompletableFuture<SignRawParams> estimateGasPrice(String jettonMasterAddress,
      String walletAddress,
      String walletPublicKey, List<String> messageList) throws TONAPIError {
    String method = String.format("v2/gasless/estimate/%s", jettonMasterAddress);

    List<Pair<String, String>> messages = messageList.stream()
        .map(msg -> new Pair<>("boc", msg))
        .collect(Collectors.toList());

    Map<String, Object> body = new HashMap<>();
    body.put("wallet_address", walletAddress);
    body.put("wallet_public_key", walletPublicKey);
    body.put("messages", messages);

    return this.post(method, null, body, null, new TypeReference<SignRawParams>() {
    });
  }

  /**
   * Send message to the blockchain.
   *
   * @param walletPublicKey The public key of the wallet.
   * @param boc             the base64 serialized bag-of-cells Example ->
   *                        te6ccgECBQEAARUAAkWIAWTtae+KgtbrX26Bep8JSq8lFLfGOoyGR/xwdjfvpvEaHg
   * @return CompletableFuture<Boolean> true if the message was sent successfully
   * @throws TONAPIError if the request fails
   */
  public CompletableFuture<Boolean> send(String walletPublicKey, String boc) throws TONAPIError {
    String method = "v2/gasless/send";

    Map<String, String> body = new HashMap<>();
    body.put("wallet_public_key", walletPublicKey);
    body.put("boc", boc);

    return this.post(method, null, body, null, new TypeReference<Boolean>() {
        })
        .thenApply(response -> true);
  }
}
