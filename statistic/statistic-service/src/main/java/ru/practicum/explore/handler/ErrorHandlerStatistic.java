package ru.practicum.explore.handler;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.explore.exceptions.InvalidRequestException;
import ru.practicum.explore.models.ApiError;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ErrorHandlerStatistic {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return new ApiError(HttpStatus.BAD_REQUEST,
                "Wrong request",
                errors,
                LocalDateTime.now().format(FORMATTER));
    }

    @ExceptionHandler(InvalidRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleInvalidRequestException(InvalidRequestException e) {
        return new ApiError(HttpStatus.BAD_REQUEST,
                "Wrong request",
                e.getMessage(),
                LocalDateTime.now().format(FORMATTER));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        return new ApiError(HttpStatus.CONFLICT,
                "The integrity constraint was violated",
                e.getMessage(),
                LocalDateTime.now().format(FORMATTER));
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleAnyException(Throwable e) {
        return new ApiError(HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL SERVER ERROR",
                e.getMessage(),
                LocalDateTime.now().format(FORMATTER));
    }
}