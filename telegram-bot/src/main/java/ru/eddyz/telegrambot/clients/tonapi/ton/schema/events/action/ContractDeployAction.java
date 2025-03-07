package ru.eddyz.telegrambot.clients.tonapi.ton.schema.events.action;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.Address;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractDeployAction {

  private Address address;
  private List<String> interfaces;
}
