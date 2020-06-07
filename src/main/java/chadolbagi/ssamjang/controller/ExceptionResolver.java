package chadolbagi.ssamjang.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionResolver {
    Logger log = LogManager.getLogger(ExceptionResolver.class);

    // @ExceptionHandler
    // public ResponseEntity<Map<String, Object>> handle(FlowException e) {
    //     return ResponseEntity.status(e.getCode()).body(Map.of("message", e.getMessage()));
    // }

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> handle(HttpServletRequest request, Exception e) {
        log.error(Map.of("url", request.getRequestURI()), e);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", e.getMessage()));
    }
}
