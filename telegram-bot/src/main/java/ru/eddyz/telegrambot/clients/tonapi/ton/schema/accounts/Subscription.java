package ru.eddyz.telegrambot.clients.tonapi.ton.schema.accounts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.Address;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.Balance;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {

  private Address address;
  private Address walletAddress;
  private Address beneficiaryAddress;
  private Balance amount;
  private Long period;
  private Long startTime;
  private Long timeout;
  private Long lastPaymentTime;
  private Long lastRequestTime;
  private Long subscriptionId;
  private Long failedAttempts;
}
