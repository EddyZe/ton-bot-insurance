package ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.sync.methods;

import com.fasterxml.jackson.core.type.TypeReference;
import ru.eddyz.telegrambot.clients.tonapi.ton.exception.TONAPIError;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.utils.AddressForm;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.utils.ServiceStatus;
import ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.sync.TonapiClientBase;

public class UtilitiesMethod extends TonapiClientBase {

  public UtilitiesMethod(TonapiClientBase client) {
    super(client);
  }

  /**
   * Parse address and display in all formats.
   *
   * @param accountId Account ID
   * @return AddressForm object containing address formats
   * @throws TONAPIError if the request fails
   */
  public AddressForm parseAddress(String accountId) throws TONAPIError {
    String method = String.format("v2/address/%s/parse", accountId);
    return this.get(method, null, null, new TypeReference<AddressForm>() {
    });
  }

  /**
   * Get service status.
   *
   * @return ServiceStatus object containing service status information
   * @throws TONAPIError if the request fails
   */
  public ServiceStatus status() throws TONAPIError {
    String method = "v2/status";
    return this.get(method, null, null, new TypeReference<ServiceStatus>() {
    });
  }
}
