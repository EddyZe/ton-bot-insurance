package ru.eddyz.telegrambot.clients.tonapi.ton.schema.wallet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressProof {

  private String address;
  private Proof proof;
}
