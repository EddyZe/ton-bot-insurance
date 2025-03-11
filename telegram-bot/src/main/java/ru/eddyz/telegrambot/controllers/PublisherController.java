package ru.eddyz.telegrambot.controllers;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.eddyz.telegrambot.domain.payloads.PublisherRequest;
import ru.eddyz.telegrambot.exception.PublisherException;
import ru.eddyz.telegrambot.services.PublisherService;
import ru.eddyz.telegrambot.util.Constant;

@RestController
@RequestMapping("publisher")
public class PublisherController {

    private final PublisherService publisherService;


    private final String botToken;

    public PublisherController(PublisherService publisherService,@Value("${telegram.bot_token}") String botToken) {
        this.publisherService = publisherService;
        this.botToken = botToken;
    }


    @PostMapping
    public ResponseEntity<String> publisher(@RequestBody PublisherRequest request, HttpServletRequest httpRequest) {
        var token = httpRequest.getHeader(Constant.BOT_TOKEN);

        if (token == null || token.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .build();

        if (!token.equals(botToken))
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .build();


        publisherService.publish(request.getHistoryId(), request.getChatId());
        return ResponseEntity.ok(HttpEntity.EMPTY.toString());
    }

    @ExceptionHandler(PublisherException.class)
    public ResponseEntity<String> handleException(PublisherException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
