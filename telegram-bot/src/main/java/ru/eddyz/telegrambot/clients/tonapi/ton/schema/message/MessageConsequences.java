package ru.eddyz.telegrambot.clients.tonapi.ton.schema.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.events.AccountEvent;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.risk.Risk;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.traces.Trace;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageConsequences {

  private Trace trace;
  private Risk risk;
  private AccountEvent event;
}
