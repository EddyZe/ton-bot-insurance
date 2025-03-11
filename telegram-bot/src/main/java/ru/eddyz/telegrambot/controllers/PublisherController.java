package ru.eddyz.telegrambot.controllers;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.eddyz.telegrambot.domain.payloads.PublisherRequest;
import ru.eddyz.telegrambot.exception.PublisherException;
import ru.eddyz.telegrambot.services.PublisherService;
import ru.eddyz.telegrambot.util.Constant;

import java.util.Map;

@RestController
@RequestMapping("publisher")
public class PublisherController {

    private final PublisherService publisherService;


    private final String botToken;

    public PublisherController(PublisherService publisherService, @Value("${telegram.bot_token}") String botToken) {
        this.publisherService = publisherService;
        this.botToken = botToken;
    }


    @PostMapping
    public ResponseEntity<?> publisher(@RequestBody PublisherRequest request, HttpServletRequest httpRequest) {
        var token = httpRequest.getHeader(Constant.BOT_TOKEN);

        if (token == null || token.isEmpty() || !token.equals(botToken))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "status", HttpStatus.UNAUTHORIZED.toString(),
                            "message", "Invalid token")
                    );

        publisherService.publish(request.getHistoryId(), request.getChatId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("status", "OK", "message", "Publisher successfully published!"));
    }

    @ExceptionHandler(PublisherException.class)
    public ResponseEntity<?> handleException(PublisherException ex) {
        return ResponseEntity.badRequest().body(
                Map.of("status", HttpStatus.BAD_REQUEST.toString(), "message", ex.getMessage())
        );
    }
}
