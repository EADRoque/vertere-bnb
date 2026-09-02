package com.vertere.messagingservice.messaging;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.vertere.messagingservice.messaging.exception.ConversationNotFoundException;

@RestControllerAdvice
public class MessagingExceptionHandler {

    @ExceptionHandler(ConversationNotFoundException.class)
    public ResponseEntity<String> handleNotFound(ConversationNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

}