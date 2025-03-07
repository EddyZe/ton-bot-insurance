package ru.eddyz.telegrambot.clients.tonapi.ton.schema.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.accounts.AccountAddress;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomainBid {

  private Boolean success;
  private Long value;

  @JsonProperty("txTime")
  private Long txTime;

  @JsonProperty("txHash")
  private String txHash;

  private AccountAddress bidder;
}
