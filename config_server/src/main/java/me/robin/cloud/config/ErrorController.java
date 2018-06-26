package me.robin.cloud.config;

import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ErrorController {
    @ExceptionHandler(Throwable.class)
    public void globalError(HttpServletResponse response, Throwable e) throws IOException {
        Map<String, Object> error = new HashMap<>();
        error.put("status", 500);
        error.put("message", e.getMessage());
        response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.getWriter().write(new JSONObject(error).toString());
    }
}
