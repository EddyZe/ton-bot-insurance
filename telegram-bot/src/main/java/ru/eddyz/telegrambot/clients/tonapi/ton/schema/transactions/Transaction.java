package ru.eddyz.telegrambot.clients.tonapi.ton.schema.transactions;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.accounts.AccountAddress;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.message.Message;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.traces.AccountStatus;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.traces.ActionPhase;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.traces.BouncePhaseType;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.traces.ComputePhase;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.traces.CreditPhase;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.traces.StoragePhase;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.traces.TransactionType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

  private String hash;
  private Long lt;
  private AccountAddress account;
  private Boolean success;
  private Long utime;
  private AccountStatus origStatus;
  private AccountStatus endStatus;
  private Long totalFees;
  private Long endBalance;
  private TransactionType transactionType;
  private String stateUpdateOld;
  private String stateUpdateNew;
  private Message inMsg;
  private List<Message> outMsgs;
  private String block;
  private String prevTransHash;
  private Long prevTransLt;
  private ComputePhase computePhase;
  private StoragePhase storagePhase;
  private CreditPhase creditPhase;
  private ActionPhase actionPhase;
  private BouncePhaseType bouncePhase;
  private Boolean aborted;
  private Boolean destroyed;
  private String raw;
}
