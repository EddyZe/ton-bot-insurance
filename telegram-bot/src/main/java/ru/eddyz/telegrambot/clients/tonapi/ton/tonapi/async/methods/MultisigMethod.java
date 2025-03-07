package ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.async.methods;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.concurrent.CompletableFuture;
import ru.eddyz.telegrambot.clients.tonapi.ton.exception.TONAPIError;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.multisig.Multisig;
import ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.async.AsyncTonapiClientBase;

public class MultisigMethod extends AsyncTonapiClientBase {

  public MultisigMethod(AsyncTonapiClientBase client) {
    super(client);
  }

  /**
   * Get multisig account info.
   *
   * @param accountId Account ID
   * @return CompletableFuture of Multisig object containing the multisig account information
   * @throws TONAPIError if the request fails
   */
  public CompletableFuture<Multisig> getAccountInfo(String accountId) throws TONAPIError {
    String method = String.format("v2/multisig/%s", accountId);
    return this.get(method, null, null, new TypeReference<Multisig>() {
    });
  }
}
