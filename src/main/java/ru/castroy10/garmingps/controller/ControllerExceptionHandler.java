package ru.castroy10.garmingps.controller;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> noResourceFoundException(final NoResourceFoundException exception, final WebRequest request) {
        final String path = request.getDescription(false).substring(4);
        if (path.equals("/favicon.ico") || path.equals("/robots.txt")) {
            return ResponseEntity.notFound().build();
        }
        log.error("Resource not found: ", exception);
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> exception(final Exception exception) {
        log.error("Ошибка ", exception);
        return ResponseEntity.internalServerError().body(Map.of("Ошибка", exception.getMessage()));
    }
}
