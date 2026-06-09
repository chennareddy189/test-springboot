package com.example;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/")
public class HelloController {

    private static final Logger log = LoggerFactory.getLogger(HelloController.class);

    @GetMapping("hello")
    public ResponseEntity<Map<String, String>> hello() {
        log.info("GET /hello called");
        return ResponseEntity.ok(Map.of("message", "Hello K8!"));
    }

    @GetMapping("health")
    public ResponseEntity<Map<String, String>> health() {
        log.info("GET /health called");
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}

