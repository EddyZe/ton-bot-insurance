package ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.async.methods;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import ru.eddyz.telegrambot.clients.tonapi.ton.exception.TONAPIError;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.tonconnect.AccountInfoByStateInit;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.tonconnect.TonconnectPayload;
import ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.async.AsyncTonapiClientBase;

public class TonconnectMethod extends AsyncTonapiClientBase {

  public TonconnectMethod(AsyncTonapiClientBase client) {
    super(client);
  }

  /**
   * Get a payload for further token receipt.
   *
   * @return CompletableFuture of TonconnectPayload object containing the payload
   * @throws TONAPIError if the request fails
   */
  public CompletableFuture<TonconnectPayload> getPayload() throws TONAPIError {
    String method = "v2/tonconnect/payload";
    return this.get(method, null, null, new TypeReference<TonconnectPayload>() {
    });
  }

  /**
   * Get account info by state init.
   *
   * @param stateInit The state init data
   * @return CompletableFuture of AccountInfoByStateInit object containing the account info
   * @throws TONAPIError if the request fails
   */
  public CompletableFuture<AccountInfoByStateInit> getInfoByStateInit(String stateInit)
      throws TONAPIError {
    String method = "v2/tonconnect/stateinit";
    Map<String, Object> body = new HashMap<>();
    body.put("state_init", stateInit);
    return this.post(method, null, body, null, new TypeReference<AccountInfoByStateInit>() {
    });
  }
}
