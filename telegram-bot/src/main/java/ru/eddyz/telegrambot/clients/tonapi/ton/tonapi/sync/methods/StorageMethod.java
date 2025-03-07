package ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.sync.methods;

import com.fasterxml.jackson.core.type.TypeReference;
import ru.eddyz.telegrambot.clients.tonapi.ton.exception.TONAPIError;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.storage.StorageProviders;
import ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.sync.TonapiClientBase;

public class StorageMethod extends TonapiClientBase {

  public StorageMethod(TonapiClientBase client) {
    super(client);
  }

  /**
   * Get TON storage providers deployed to the blockchain.
   *
   * @return StorageProviders object containing storage provider information
   * @throws TONAPIError if the request fails
   */
  public StorageProviders getProviders() throws TONAPIError {
    String method = "v2/storage/providers";
    return this.get(method, null, null, new TypeReference<StorageProviders>() {
    });
  }
}
