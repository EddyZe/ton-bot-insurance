package ru.eddyz.telegrambot.clients.tonapi.ton.schema.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.Address;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEventData {

  private Address accountId;
  private Long lt;
  private String txHash;
}
