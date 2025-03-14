package ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.async.methods;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import ru.eddyz.telegrambot.clients.tonapi.ton.exception.TONAPIError;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.blockchain.DecodedMessage;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.events.AccountEvent;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.events.Event;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.message.MessageConsequences;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.traces.Trace;
import ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.async.AsyncTonapiClientBase;

public class EmulateMethod extends AsyncTonapiClientBase {

  public EmulateMethod(AsyncTonapiClientBase client) {
    super(client);
  }

  /**
   * Decode a given message. Only external incoming messages can be decoded currently.
   *
   * @param boc the base64 serialized bag-of-cells Example ->
   *            te6ccgECBQEAARUAAkWIAWTtae+KgtbrX26Bep8JSq8lFLfGOoyGR/xwdjfvpvEaHg
   * @return CompletableFuture of DecodedMessage object containing the decoded message.
   * @throws TONAPIError if the request fails
   */
  public CompletableFuture<DecodedMessage> decodeMessage(String boc) throws TONAPIError {
    String method = "v2/message/decode";

    Map<String, String> body = new HashMap<>();
    body.put("boc", boc);

    return this.post(method, null, body, null, new TypeReference<DecodedMessage>() {
    });
  }

  /**
   * Emulate sending a message to the blockchain and get the event.
   *
   * @param boc                  the base64 serialized bag-of-cells Example ->
   *                             te6ccgECBQEAARUAAkWIAWTtae+KgtbrX26Bep8JSq8lFLfGOoyGR/xwdjfvpvEaHg
   * @param acceptLanguage       Accept-Language header value. Default is "en". Example ->
   *                             ru-RU,ru;q=0.5
   * @param ignoreSignatureCheck Optional parameter to ignore signature check.
   * @return CompletableFuture of Event object containing the emulated event.
   * @throws TONAPIError if the request fails
   */
  public CompletableFuture<Event> emulateEvents(
      String boc,
      String acceptLanguage,
      Boolean ignoreSignatureCheck) throws TONAPIError {
    String method = "v2/events/emulate";

    Map<String, String> body = new HashMap<>();
    body.put("boc", boc);

    Map<String, Object> params = new HashMap<>();
    if (ignoreSignatureCheck != null) {
      params.put("ignore_signature_check", ignoreSignatureCheck);
    }
    Map<String, String> headers = new HashMap<>();
    headers.put("Accept-Language", acceptLanguage != null ? acceptLanguage : "en");
    return this.post(method, params.isEmpty() ? null : params, body, headers,
        new TypeReference<Event>() {
        });
  }

  /**
   * Emulate sending a message to the blockchain and get the trace.
   *
   * @param boc                  the base64 serialized bag-of-cells Example ->
   *                             te6ccgECBQEAARUAAkWIAWTtae+KgtbrX26Bep8JSq8lFLfGOoyGR/xwdjfvpvEaHg
   * @param ignoreSignatureCheck Optional parameter to ignore signature check.
   * @return CompletableFuture of Trace object containing the emulated trace.
   * @throws TONAPIError if the request fails
   */
  public CompletableFuture<Trace> emulateTraces(
      String boc,
      Boolean ignoreSignatureCheck) throws TONAPIError {
    String method = "v2/traces/emulate";

    Map<String, String> body = new HashMap<>();
    body.put("boc", boc);

    Map<String, Object> params = new HashMap<>();
    if (ignoreSignatureCheck != null) {
      params.put("ignore_signature_check", ignoreSignatureCheck);
    }
    return this.post(method, params.isEmpty() ? null : params, body, null,
        new TypeReference<Trace>() {
        });
  }

  /**
   * Emulate sending a message to the blockchain as a wallet.
   *
   * @param boc            the base64 serialized bag-of-cells Example ->
   *                       te6ccgECBQEAARUAAkWIAWTtae+KgtbrX26Bep8JSq8lFLfGOoyGR/xwdjfvpvEaHg
   * @param acceptLanguage Accept-Language header value. Default is "en". Example -> ru-RU,ru;q=0.5
   * @return CompletableFuture of MessageConsequences object containing the consequences of the
   * message.
   * @throws TONAPIError if the request fails
   */
  public CompletableFuture<MessageConsequences> emulateWallet(
      String boc,
      String acceptLanguage) throws TONAPIError {
    String method = "v2/wallet/emulate";

    Map<String, String> body = new HashMap<>();
    body.put("boc", boc);

    Map<String, String> headers = new HashMap<>();
    headers.put("Accept-Language", acceptLanguage != null ? acceptLanguage : "en");
    return this.post(method, null, body, headers, new TypeReference<MessageConsequences>() {
    });
  }

  /**
   * Emulate sending a message to the blockchain for a specific account and get the account event.
   *
   * @param accountId      Account ID
   * @param boc            the base64 serialized bag-of-cells Example ->
   *                       te6ccgECBQEAARUAAkWIAWTtae+KgtbrX26Bep8JSq8lFLfGOoyGR/xwdjfvpvEaHg
   * @param acceptLanguage Accept-Language header value. Default is "en". Example -> ru-RU,ru;q=0.5
   * @return CompletableFuture of AccountEvent object containing the emulated account event.
   * @throws TONAPIError if the request fails
   */
  public CompletableFuture<AccountEvent> emulateAccountEvent(
      String accountId,
      String boc,
      String acceptLanguage) throws TONAPIError {
    String method = String.format("v2/accounts/%s/events/emulate", accountId);

    Map<String, String> body = new HashMap<>();
    body.put("boc", boc);

    Map<String, String> headers = new HashMap<>();
    headers.put("Accept-Language", acceptLanguage != null ? acceptLanguage : "en");
    return this.post(method, null, body, headers, new TypeReference<AccountEvent>() {
    });
  }
}
