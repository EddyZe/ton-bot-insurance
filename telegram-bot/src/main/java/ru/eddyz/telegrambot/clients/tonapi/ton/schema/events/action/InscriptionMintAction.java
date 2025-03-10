package ru.eddyz.telegrambot.clients.tonapi.ton.schema.events.action;

import java.math.BigInteger;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.accounts.AccountAddress;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InscriptionMintAction {

  private AccountAddress recipient;
  private BigInteger amount;
  private String type;
  private String ticker;
  private Integer decimals;
}
