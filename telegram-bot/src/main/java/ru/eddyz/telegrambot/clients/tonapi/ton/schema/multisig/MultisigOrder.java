package ru.eddyz.telegrambot.clients.tonapi.ton.schema.multisig;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.Address;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.risk.Risk;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MultisigOrder {

  private Address address;
  private Long orderSeqno;
  private Long threshold;
  private boolean sentForExecution;
  private List<Address> signers;
  private Long approvalsNum;
  private Long expirationData;
  private Risk risk;
}
