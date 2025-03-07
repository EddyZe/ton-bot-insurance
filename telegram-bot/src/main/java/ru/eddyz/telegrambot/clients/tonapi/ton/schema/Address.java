package ru.eddyz.telegrambot.clients.tonapi.ton.schema;

import lombok.Value;
import ru.eddyz.telegrambot.clients.tonapi.ton.util.Utils;

@Value
public class Address {

  String value;

  @Override
  public String toString() {
    return value;
  }

  public String toRaw() {
    return value;
  }

  public String toUserFriendly(boolean isBounceable) {
    return Utils.rawToUserFriendly(value, isBounceable);
  }
}
