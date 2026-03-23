package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValid(final MethodArgumentNotValidException e) {
        log.error("Ошибка валидации 400: {}", e.getMessage());
        String errorMessage = Objects.requireNonNull(e.getFieldError()).getDefaultMessage();
        return new ErrorResponse("Ошибка валидации", errorMessage);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadable(final HttpMessageNotReadableException e) {
        log.error("Ошибка чтения запроса 400: {}", e.getMessage());
        return new ErrorResponse("Ошибка запроса", "Некорректные данные в запросе");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.error("Непредвиденная ошибка 500: {}", e.getMessage(), e);
        return new ErrorResponse("Внутренняя ошибка сервера", e.getMessage());
    }
}