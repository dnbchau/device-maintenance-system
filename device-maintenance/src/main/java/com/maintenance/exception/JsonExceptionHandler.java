package com.maintenance.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.maintenance.dto.response.ApiResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JsonExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleJsonParse(HttpMessageNotReadableException ex) {
        Map<String, String> errors = new HashMap<>();
        Throwable cause = ex.getCause();

        if (cause instanceof InvalidFormatException invalidFormatException) {
            String field = invalidFormatException.getPath().isEmpty()
                    ? "requestBody"
                    : invalidFormatException.getPath().get(invalidFormatException.getPath().size() - 1).getFieldName();

            if (invalidFormatException.getTargetType() == LocalDateTime.class) {
                errors.put(field, "Sai dinh dang ngay gio. Dung yyyy-MM-dd'T'HH:mm:ss, vi du 2024-01-22T10:00:00.");
            } else {
                errors.put(field, "Gia tri khong dung kieu du lieu yeu cau.");
            }
        } else {
            errors.put("requestBody", "JSON khong hop le hoac thieu dau phan tach.");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Map<String, String>>builder()
                        .success(false)
                        .message("Du lieu request khong hop le")
                        .data(errors)
                        .build());
    }
}
