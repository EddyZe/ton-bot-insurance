package ru.eddyz.telegrambot.clients.tonapi.ton.exception;

public class TONAPISSELimitReachedError extends TONAPISSEError {

  public TONAPISSELimitReachedError(String message) {
    super(message);
  }
}
