package ru.eddyz.telegrambot.clients.tonapi.ton.schema.accounts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicKey {

  private String publicKey;
}
