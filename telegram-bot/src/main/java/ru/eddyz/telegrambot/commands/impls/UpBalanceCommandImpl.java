package ru.eddyz.telegrambot.commands.impls;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.eddyz.telegrambot.commands.UpBalanceCommand;
import ru.eddyz.telegrambot.util.Sender;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
public class UpBalanceCommandImpl implements UpBalanceCommand {


    @Value("${insurance.admin_wallet}")
    private String adminWallet;

    @Value("${insurance.token.name}")
    private String tokenName;

    private final TelegramClient telegramClient;

    private final static String qrCodeFilePath = "qrcode-%s.png";


    @Override
    public void execute(CallbackQuery callbackQuery) {
        var chatId = callbackQuery.getFrom().getId();

        String comment = URLEncoder.encode(chatId.toString(), StandardCharsets.UTF_8);
        String qrCodeData = """
                ton://transfer/%s?text=%s""".formatted(adminWallet, comment);

        answerCallBack(callbackQuery.getId());

        try {
            var qr = generateQRCode(qrCodeData, qrCodeFilePath.formatted(chatId));

            var message = "Отправьте сумму %s на которую хотите пополнить счет по данному QR коду. \n\n❗Укажите <b>%s</b> этот код в комментарии к платежу, если он не установился автоматически! \n\n❗ Внимание! Переводить нужно токен <b>%s</b>, иначе средства не будут начислены!";
            sendPhoto(chatId, qr, message.formatted(tokenName, chatId, tokenName));
            sendMessage(chatId, "<b>" + chatId + "</b>");
            deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
            try {
                Files.delete(qr.toPath());
            } catch (IOException e) {
                log.error("Error remove qrcode file", e);
            }
        } catch (Exception e) {
            log.error("Failed to generate QR code", e);
            sendMessage(chatId, "Произошла ошибка при попытке пополнить счет. Попробуйте повторить попытку!");
        }
    }

    private void deleteMessage(Long chatId, Integer messageId) {
        try {
            telegramClient.execute(Sender.deleteMessage(chatId, messageId));
        } catch (TelegramApiException e) {
            log.error("Failed to delete message", e);
        }
    }

    private void answerCallBack(String id) {
        try {
            telegramClient.execute(new AnswerCallbackQuery(id));
        } catch (TelegramApiException e) {
            log.error("Failed to answer callback", e);
        }
    }

    private void sendPhoto(Long chatId, File file, String text) {
        try {
            var sendPhoto = SendPhoto.builder()
                    .chatId(chatId)
                    .caption(text)
                    .parseMode(ParseMode.HTML)
                    .photo(new InputFile(file))
                    .build();

            telegramClient.execute(sendPhoto);
        } catch (TelegramApiException e) {
            log.error("Failed to send photo", e);
        }
    }

    private void sendMessage(Long chatId, String message) {
        try {
            var sendMessage = Sender.sendMessage(chatId, message);
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Failed to send message UpBalanceCommand", e);
        }
    }

    private File generateQRCode(String data, String filePath) throws Exception {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 1);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 300, 300, hints);

        BufferedImage image = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < 300; x++) {
            for (int y = 0; y < 300; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0x000000 : 0xFFFFFF);
            }
        }

        File qrCodeFile = new File(filePath);
        ImageIO.write(image, "PNG", qrCodeFile);

        return qrCodeFile;
    }
}
