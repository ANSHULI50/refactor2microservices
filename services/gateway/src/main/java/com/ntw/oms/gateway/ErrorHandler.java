package com.ntw.oms.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/error")
public class ErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(ErrorHandler.class);

    @GetMapping(path = "/cb", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> handleCircuitBreakerError() {
        logger.info("Inside circuit breaker fallback handler.");
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("\nRequest timed out. Try again later!\n");
    }

}
