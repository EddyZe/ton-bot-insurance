package ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.async.methods;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import ru.eddyz.telegrambot.clients.tonapi.ton.exception.TONAPIError;
import ru.eddyz.telegrambot.clients.tonapi.ton.schema.traces.Trace;
import ru.eddyz.telegrambot.clients.tonapi.ton.tonapi.async.AsyncTonapiClientBase;

public class TracesMethod extends AsyncTonapiClientBase {

  public TracesMethod(AsyncTonapiClientBase client) {
    super(client);
  }

  /**
   * Get the trace by trace ID or hash of any transaction in trace.
   *
   * @param traceId Trace ID or transaction hash in hex (without 0x) or base64url format
   * @return CompletableFuture of Trace object containing the trace data
   * @throws TONAPIError if the request fails
   */
  public CompletableFuture<Trace> getTrace(String traceId) throws TONAPIError {
    if (traceId.length() == 44) {
      byte[] decodedBytes = Base64.getUrlDecoder().decode(traceId);
      StringBuilder hexString = new StringBuilder();
      for (byte b : decodedBytes) {
        String hex = String.format("%02x", b);
        hexString.append(hex);
      }
      traceId = hexString.toString();
    }
    String method = String.format("v2/traces/%s", traceId);
    return this.get(method, null, null, new TypeReference<Trace>() {
    });
  }

  /**
   * Emulate sending message to blockchain.
   *
   * @param boc                  the base64 serialized bag-of-cells Example ->
   *                             te6ccgECBQEAARUAAkWIAWTtae+KgtbrX26Bep8JSq8lFLfGOoyGR/xwdjfvpvEaHg
   * @param ignoreSignatureCheck Optional parameter to ignore signature check
   * @return CompletableFuture of Trace object containing the emulated trace
   * @throws TONAPIError if the request fails
   */
  public CompletableFuture<Trace> emulate(String boc, Boolean ignoreSignatureCheck)
      throws TONAPIError {
    String method = "v2/traces/emulate";

    Map<String, String> body = new HashMap<>();
    body.put("boc", boc);

    Map<String, Object> params = new HashMap<>();
    if (ignoreSignatureCheck != null && ignoreSignatureCheck) {
      params.put("ignore_signature_check", ignoreSignatureCheck);
    }
    return this.post(method, params.isEmpty() ? null : params, body, null,
        new TypeReference<Trace>() {
        });
  }
}
