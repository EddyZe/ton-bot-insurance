package ru.eddyz.telegrambot.clients.tonapi.ton.schema.events.action;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.accounts.AccountAddress;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositStakeAction {

  private Long amount;
  private AccountAddress staker;
  private AccountAddress pool;
}
