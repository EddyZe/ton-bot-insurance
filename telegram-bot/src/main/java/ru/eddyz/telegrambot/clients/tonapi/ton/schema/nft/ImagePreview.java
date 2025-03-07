package ru.eddyz.telegrambot.clients.tonapi.ton.schema.nft;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImagePreview {

  private String resolution;
  private String url;
}
