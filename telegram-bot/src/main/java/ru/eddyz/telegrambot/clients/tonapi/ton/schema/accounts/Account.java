package ru.eddyz.telegrambot.clients.tonapi.ton.schema.accounts;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.Address;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.Balance;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

  private Address address;
  private Balance balance;
  private String status;
  private List<String> interfaces;
  private String name;
  private String icon;
  private List<String> getMethods;
  private Boolean memoRequired;
  private Boolean isScam;
  private Long lastActivity;
  private Boolean isSuspended;
  private Boolean isWallet;
}
