package ru.eddyz.telegrambot.clients.tonapi.ton.schema.transactions;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transactions {

  private List<Transaction> transactions;
}
