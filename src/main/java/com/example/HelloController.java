package com.example;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/")
public class HelloController {

    @GetMapping("hello")
    public ResponseEntity<Map<String, String>> hello() {
        return ResponseEntity.ok(Map.of("message", "Hello, world!"));
    }

    @GetMapping("health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}

