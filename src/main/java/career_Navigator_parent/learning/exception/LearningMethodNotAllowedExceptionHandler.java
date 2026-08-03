package career_Navigator_parent.learning.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class LearningMethodNotAllowedExceptionHandler {

    @ExceptionHandler(
            HttpRequestMethodNotSupportedException.class
    )
    public ResponseEntity<Map<String, Object>> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException exception,
            HttpServletRequest request
    ) {
        Map<String, Object> body =
                new LinkedHashMap<>();

        body.put(
                "timestamp",
                LocalDateTime.now()
        );

        body.put(
                "status",
                HttpStatus.METHOD_NOT_ALLOWED.value()
        );

        body.put(
                "error",
                HttpStatus.METHOD_NOT_ALLOWED
                        .getReasonPhrase()
        );

        body.put(
                "message",
                "HTTP method "
                        + exception.getMethod()
                        + " is not supported for this endpoint."
        );

        body.put(
                "supportedMethods",
                exception.getSupportedHttpMethods()
        );

        body.put(
                "path",
                request.getRequestURI()
        );

        return ResponseEntity
                .status(
                        HttpStatus.METHOD_NOT_ALLOWED
                )
                .body(body);
    }
}
