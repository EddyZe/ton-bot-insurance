package ru.eddyz.telegrambot.clients.tonapi.ton.schema.inscriptions;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InscriptionBalances {

  private List<InscriptionBalance> inscriptions;
}
