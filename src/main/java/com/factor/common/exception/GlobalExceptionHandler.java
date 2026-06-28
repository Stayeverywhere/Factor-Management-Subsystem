package com.factor.common.exception;

import com.factor.common.api.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex) {
        return ResponseEntity.badRequest().body(ApiResponse.fail(ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ApiResponse.fail(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpectedException(Exception ex) {
        String msg = ex.getMessage();
        if (msg == null) {
            Throwable cause = ex.getCause();
            while (cause != null) {
                if (cause.getMessage() != null) {
                    msg = cause.getMessage();
                    break;
                }
                cause = cause.getCause();
            }
        }
        if (msg == null) msg = ex.getClass().getSimpleName();
        ex.printStackTrace();
        return ResponseEntity.internalServerError().body(ApiResponse.fail("Internal server error: " + msg));
    }
}
