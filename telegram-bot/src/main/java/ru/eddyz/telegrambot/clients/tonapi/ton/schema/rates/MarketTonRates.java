package ru.eddyz.telegrambot.clients.tonapi.ton.schema.rates;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketTonRates {

  private String market;
  private Double usdPrice;
  private Long lastDateUpdate;
}
