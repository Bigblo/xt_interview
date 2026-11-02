package com.xt.interview.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 全局业务异常处理。
 *
 * <p>service 层统一用 RuntimeException 表达业务错误（如“部门编码已存在”），
 * 这里把它转成 400 + {@code {success:false, message}}，前端 request.ts 可直接取 message 提示。</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常。
     *
     * @param e 业务异常
     * @return 400 响应，body 含 success=false 与 message
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException e) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("message", e.getMessage() == null ? "操作失败" : e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
