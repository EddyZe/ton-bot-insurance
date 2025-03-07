package ru.eddyz.telegrambot.clients.tonapi.ton.schema.dns;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.Address;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.accounts.Account;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletDNS {

  private Address address;
  private Boolean isWallet;
  private Boolean hasMethodPubkey;
  private Boolean hasMethodSeqno;
  private List<String> names;
  private Account account;
}
