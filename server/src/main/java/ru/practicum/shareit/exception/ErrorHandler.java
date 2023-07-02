package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse missingRequestHeaderExceptionHandler(final MissingRequestHeaderException ex) {
        log.error("Request header error: {}", ex.getMessage());
        return ErrorResponse.getFromException(ex);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> methodArgumentNotValidException(final MethodArgumentNotValidException e) {
        return Map.of("User exception: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse incorrectDataHandler(final IncorrectDataException ex) {
        log.error("Data error: {}.", ex.getMessage());
        return ErrorResponse.getFromException(ex);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse entityNotFoundExceptionHandler(final EntityNotFoundException ex) {
        log.error("Search error: {}.", ex.getMessage());
        return ErrorResponse.getFromException(ex);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse logicExceptionHandler(final LogicException ex) {
        log.error("Logic error: {}.", ex.getMessage());
        return ErrorResponse.getFromException(ex);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse dataIntegrityViolationExceptionHandler(final DataIntegrityViolationException ex) {
        log.error("Database error: {}.", ex.getMostSpecificCause().getMessage());
        return ErrorResponse.getFromException(ex.getMostSpecificCause());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse throwableHandler(final Throwable th) {
        log.error("Unexpected error: {}.", th.getMessage());
        return ErrorResponse.getFromException(th);
    }
}
