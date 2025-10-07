package ru.castroy10.garmingps.controller;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> exception(final Exception exception) {
        log.error("Ошибка ", exception);
        return ResponseEntity.internalServerError().body(Map.of("Ошибка", exception.getMessage()));
    }
}
