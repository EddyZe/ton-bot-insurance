package ru.eddyz.telegrambot.clients.tonapi.ton.schema.nft;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.Address;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Collection {

  private Address address;
  private String name;
  private String description;
}
