package ru.eddyz.telegrambot.clients.tonapi.ton.schema.events.action;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.accounts.AccountAddress;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NftItemTransferAction {

  private AccountAddress sender;
  private AccountAddress recipient;
  private String nft;
  private String comment;
  private EncryptedComment encryptedComment;
  private String payload;
  private Refund refund;
}
