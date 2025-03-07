package ru.eddyz.telegrambot.clients.tonapi.ton.schema.stacking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StakingPoolInfo {

  private PoolImplementation implementation;
  private PoolInfo pool;
}
