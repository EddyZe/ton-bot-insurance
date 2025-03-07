package ru.eddyz.telegrambot.clients.tonapi.ton.schema.events.action;

import java.math.BigInteger;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.accounts.AccountAddress;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.jettons.JettonPreview;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JettonBurnAction {

  private AccountAddress sender;
  private String sendersWallet;
  private BigInteger amount;
  private JettonPreview jetton;
}
