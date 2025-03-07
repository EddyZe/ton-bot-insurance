package ru.eddyz.telegrambot.clients.tonapi.ton.schema.liteserver;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawBlockHeader {

  private BlockRaw id;
  private Integer mode;
  private String headerProof;
}
