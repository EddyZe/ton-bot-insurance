package ru.eddyz.telegrambot.clients.tonapi.ton.schema.jettons;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.Address;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class JettonPreview {

  private Address address;
  private String name;
  private String symbol;
  private Integer decimals;
  private String image;
  private JettonVerificationType verification;
}
