package ru.eddyz.telegrambot.clients.tonapi.ton.schema.rates;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketsTonRates {

  private List<MarketTonRates> markets;
}
