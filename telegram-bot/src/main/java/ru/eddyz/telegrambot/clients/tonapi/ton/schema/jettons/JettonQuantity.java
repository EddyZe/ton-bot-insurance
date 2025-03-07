package ru.eddyz.telegrambot.clients.tonapi.ton.schema.jettons;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.accounts.AccountAddress;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JettonQuantity {

  private String quantity;
  private AccountAddress walletAddress;
  private JettonPreview jetton;
}
