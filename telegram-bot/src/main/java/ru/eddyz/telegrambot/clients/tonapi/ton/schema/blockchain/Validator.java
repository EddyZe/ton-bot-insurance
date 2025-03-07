package ru.eddyz.telegrambot.clients.tonapi.ton.schema.blockchain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.Address;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Validator {

  private Address address;
  private String adnlAddress;
  private Long stake;
  private Long maxFactor;
}
