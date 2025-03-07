package ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.async.methods;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.concurrent.CompletableFuture;
import ru.eddyz.telegrambot.clients.tonapi.ton.exception.TONAPIError;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.utils.AddressForm;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.utils.ServiceStatus;
import ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.async.AsyncTonapiClientBase;

public class UtilitiesMethod extends AsyncTonapiClientBase {

  public UtilitiesMethod(AsyncTonapiClientBase client) {
    super(client);
  }

  /**
   * Parse address and display in all formats.
   *
   * @param accountId Account ID
   * @return CompletableFuture of AddressForm object containing address formats
   * @throws TONAPIError if the request fails
   */
  public CompletableFuture<AddressForm> parseAddress(String accountId) throws TONAPIError {
    String method = String.format("v2/address/%s/parse", accountId);
    return this.get(method, null, null, new TypeReference<AddressForm>() {
    });
  }

  /**
   * Get service status.
   *
   * @return CompletableFuture of ServiceStatus object containing service status information
   * @throws TONAPIError if the request fails
   */
  public CompletableFuture<ServiceStatus> status() throws TONAPIError {
    String method = "v2/status";
    return this.get(method, null, null, new TypeReference<ServiceStatus>() {
    });
  }
}
