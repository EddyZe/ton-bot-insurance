package ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.sync.methods;

import com.fasterxml.jackson.core.type.TypeReference;
import ru.eddyz.telegrambot.clients.tonapi.ton.exception.TONAPIError;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.multisig.Multisig;
import ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.sync.TonapiClientBase;

public class MultisigMethod extends TonapiClientBase {

  public MultisigMethod(TonapiClientBase client) {
    super(client);
  }

  /**
   * Get multisig account info.
   *
   * @param accountId Account ID
   * @return Multisig object containing the multisig account information
   * @throws TONAPIError if the request fails
   */
  public Multisig getAccountInfo(String accountId) throws TONAPIError {
    String method = String.format("v2/multisig/%s", accountId);
    return this.get(method, null, null, new TypeReference<Multisig>() {
    });
  }
}
