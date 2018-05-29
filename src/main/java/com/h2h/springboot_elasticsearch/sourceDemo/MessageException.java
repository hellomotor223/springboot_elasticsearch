package com.h2h.springboot_elasticsearch.sourceDemo;

public class MessageException extends Exception {
    public MessageException() {
    }

    public MessageException(String message) {
        super(message);
    }

    public MessageException(String message,Exception e) {
        super(message);
        e.printStackTrace();
    }
}
