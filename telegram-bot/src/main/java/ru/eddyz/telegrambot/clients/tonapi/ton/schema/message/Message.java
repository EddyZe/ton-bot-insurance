package ru.eddyz.telegrambot.clients.tonapi.ton.schema.message;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.accounts.AccountAddress;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.traces.StateInit;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

  private String msgType;
  private Long createdLt;
  private boolean ihrDisabled;
  private boolean bounce;
  private boolean bounced;
  private Long value;
  private Long fwdFee;
  private Long ihrFee;
  private AccountAddress destination;
  private AccountAddress source;
  private Long importFee;
  private Long createdAt;
  private String opCode;
  private StateInit init;
  private String hash;
  private String rawBody;
  private String decodedOpName;
  private Map<String, Object> decodedBody;
}
