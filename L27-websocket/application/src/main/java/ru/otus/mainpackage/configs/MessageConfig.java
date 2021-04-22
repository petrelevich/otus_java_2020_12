package ru.otus.mainpackage.configs;

public class MessageConfig {

    private final String message;

    public MessageConfig(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ApplicationConfig{" +
                "message='" + message + '\'' +
                '}';
    }
}
