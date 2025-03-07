package ru.eddyz.telegrambot.clients.tonapi.ton.schema.traces;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TraceId {

  private String id;
  private Long utime;
}
