package ru.eddyz.telegrambot.clients.tonapi.ton.schema.accounts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.nft.NftItem;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DnsExpiringItemsInner {

  private Long expiringAt;
  private String name;
  private NftItem dnsItem;
}
