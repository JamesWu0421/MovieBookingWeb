package tw.com.ispan.exceptionhandle;


import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN) // 403
    public Map<String, String> handleAccessDenied(AccessDeniedException ex) {
        return Map.of(
                "error", "FORBIDDEN",
                "message", ex.getMessage()
        );
    }
}
