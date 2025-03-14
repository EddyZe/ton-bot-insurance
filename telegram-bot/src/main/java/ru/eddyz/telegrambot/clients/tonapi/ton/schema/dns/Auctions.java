package ru.eddyz.telegrambot.clients.tonapi.ton.schema.dns;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Auctions {

  private List<Auction> data;
  private Long total;
}
