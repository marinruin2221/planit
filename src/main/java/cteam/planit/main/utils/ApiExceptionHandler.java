package cteam.planit.main.utils;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException e) {
        if ("EVENT_NOT_FOUND".equals(e.getMessage())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "EVENT_NOT_FOUND"));
        }
        return ResponseEntity.badRequest()
                .body(Map.of("message", e.getMessage()));
    }
}
