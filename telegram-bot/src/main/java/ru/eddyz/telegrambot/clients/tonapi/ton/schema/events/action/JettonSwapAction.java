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
public class JettonSwapAction {

  private String dex;
  private BigInteger amountIn;
  private BigInteger amountOut;
  private Integer tonIn;
  private Integer tonOut;
  private AccountAddress userWallet;
  private AccountAddress router;
  private JettonPreview jettonMasterIn;
  private JettonPreview jettonMasterOut;
}
