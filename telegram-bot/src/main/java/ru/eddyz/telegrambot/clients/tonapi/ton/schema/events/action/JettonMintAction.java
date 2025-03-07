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
public class JettonMintAction {

  private AccountAddress recipient;
  private String recipientsWallet;
  private BigInteger amount;
  private JettonPreview jetton;
}
