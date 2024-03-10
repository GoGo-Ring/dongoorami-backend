package com.gogoring.dongoorami.concert.exception;

import com.gogoring.dongoorami.global.exception.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ConcertGlobalExceptionHandler {

    @ExceptionHandler(ConcertNotFoundException.class)
    public ResponseEntity<ErrorResponse> catchConcertNotFoundException(ConcertNotFoundException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
    }
}
