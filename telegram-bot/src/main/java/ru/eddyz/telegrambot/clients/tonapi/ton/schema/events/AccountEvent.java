package ru.eddyz.telegrambot.clients.tonapi.ton.schema.events;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.accounts.AccountAddress;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.events.action.Action;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountEvent {

  private String description;
  private AccountAddress account;
  private Long timestamp;
  private List<Action> actions;
  private Boolean isScam;
  private Long lt;
  private Boolean inProgress;
  private Long extra;
  private String eventId;
}
