//package careerpilot_parent.security.handler;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//import lombok.RequiredArgsConstructor;
//
//import org.springframework.http.MediaType;
//
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.security.web.access.AccessDeniedHandler;
//
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.util.LinkedHashMap;
//import java.util.Map;
//
//@Component
//@RequiredArgsConstructor
//public class JwtAccessDeniedHandler
//        implements AccessDeniedHandler {
//
//    private final ObjectMapper objectMapper;
//
//    @Override
//    public void handle(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            AccessDeniedException exception
//    ) throws IOException, ServletException {
//
//        response.setStatus(
//                HttpServletResponse.SC_FORBIDDEN
//        );
//
//        response.setContentType(
//                MediaType.APPLICATION_JSON_VALUE
//        );
//
//        response.setCharacterEncoding("UTF-8");
//
//        Map<String, Object> body =
//                new LinkedHashMap<>();
//
//        body.put(
//                "timestamp",
//                LocalDateTime.now()
//        );
//
//        body.put(
//                "status",
//                HttpServletResponse.SC_FORBIDDEN
//        );
//
//        body.put(
//                "error",
//                "Forbidden"
//        );
//
//        body.put(
//                "message",
//                "You do not have permission to access this resource."
//        );
//
//        body.put(
//                "path",
//                request.getRequestURI()
//        );
//
//        objectMapper.writeValue(
//                response.getOutputStream(),
//                body
//        );
//    }
//}
package careerpilot_parent.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler
        implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException exception
    ) throws IOException, ServletException {

        response.setStatus(
                HttpServletResponse.SC_FORBIDDEN
        );

        response.setContentType(
                MediaType.APPLICATION_JSON_VALUE
        );

        response.setCharacterEncoding("UTF-8");

        Map<String, Object> body =
                new LinkedHashMap<>();

        body.put(
                "timestamp",
                LocalDateTime.now()
        );

        body.put(
                "status",
                HttpServletResponse.SC_FORBIDDEN
        );

        body.put(
                "error",
                "Forbidden"
        );

        body.put(
                "message",
                "You do not have permission to access this resource."
        );

        body.put(
                "path",
                request.getRequestURI()
        );

        objectMapper.writeValue(
                response.getOutputStream(),
                body
        );
    }
}