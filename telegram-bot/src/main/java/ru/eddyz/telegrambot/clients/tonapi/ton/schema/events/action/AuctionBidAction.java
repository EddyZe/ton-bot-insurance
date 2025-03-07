package ru.eddyz.telegrambot.clients.tonapi.ton.schema.events.action;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.accounts.AccountAddress;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.nft.NftItem;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.nft.Price;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuctionBidAction {

  private String auctionType;
  private Price amount;
  private NftItem nft;
  private AccountAddress bidder;
  private AccountAddress auction;
}
