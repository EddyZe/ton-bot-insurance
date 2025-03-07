package ru.eddyz.telegrambot.clients.tonapi.ton.schema.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.nft.NftItem;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomainInfo {

  private String name;
  private Integer expiringAt;
  private NftItem item;
}
