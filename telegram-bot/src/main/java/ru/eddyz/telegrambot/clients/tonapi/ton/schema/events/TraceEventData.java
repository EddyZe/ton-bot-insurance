package ru.eddyz.telegrambot.clients.tonapi.ton.schema.events;

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
public class TraceEventData {

  private List<Address> accounts;
  private String hash;
}
