package ru.eddyz.telegrambot.clients.tonapi.ton.schema.liteserver;

import java.util.LinkedHashMap;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawShardProof {

  private BlockRaw masterchainId;
  private List<LinkedHashMap<String, Object>> links;
}
