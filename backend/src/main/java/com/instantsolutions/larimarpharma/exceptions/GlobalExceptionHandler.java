package com.instantsolutions.larimarpharma.exceptions;

import com.instantsolutions.larimarpharma.DTOs.ErrorResponseDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFound(
            EntityNotFoundException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDto(
                        "RESOURCE_NOT_FOUND",
                        ex.getMessage(),
                        request.getRequestURI(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDto(
                        "RESOURCE_NOT_FOUND",
                        ex.getMessage(),
                        request.getRequestURI(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponseDto> handleBadRequest(
            BadRequestException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto(
                        "BAD_REQUEST",
                        ex.getMessage(),
                        request.getRequestURI(),
                        LocalDateTime.now()
                ));
    }
    @ExceptionHandler(InsufficientLeaveBalanceException.class)
    public ResponseEntity<ErrorResponseDto> handleLeaveBalance(
            InsufficientLeaveBalanceException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto(
                        "INSUFFICIENT_LEAVE_BALANCE",
                        ex.getMessage(),
                        request.getRequestURI(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneric(
            Exception ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto(
                        "INTERNAL_SERVER_ERROR",
                        "Something went wrong. Please try again later.",
                        request.getRequestURI(),
                        LocalDateTime.now()
                ));
    }
}
