package ru.eddyz.telegrambot.exception;

public class PublisherException extends RuntimeException{
    public PublisherException(Exception msg) {
        super(msg);
    }

    public PublisherException(String message) {
        super(message);
    }
}
