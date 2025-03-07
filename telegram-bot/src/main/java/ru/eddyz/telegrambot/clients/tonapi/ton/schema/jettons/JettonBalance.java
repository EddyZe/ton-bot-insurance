package ru.eddyz.telegrambot.clients.tonapi.ton.schema.jettons;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.accounts.AccountAddress;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.rates.TokenRates;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class JettonBalance {

  private String balance;
  private TokenRates price;
  private AccountAddress walletAddress;
  private JettonPreview jetton;
}
