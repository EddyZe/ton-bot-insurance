package ru.eddyz.telegrambot.clients.tonapi.ton.schema.nft;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.Address;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.accounts.AccountAddress;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NftCollection {

  private Address address;
  private Long nextItemIndex;
  private AccountAddress owner;
  private String rawCollectionContent;
  private Map<String, Object> metadata;
  private List<ImagePreview> previews;
  private List<String> approvedBy; // getgems, tonkeeper, ton.diamonds, none
}
