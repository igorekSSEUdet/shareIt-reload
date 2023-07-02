package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public final class ErrorResponse {
    private final String error;
    private final String exception;

    private ErrorResponse(String message, String exception) {
        this.error = message;
        this.exception = exception;
    }

    public static ErrorResponse getFromException(Throwable th) {
        return new ErrorResponse(th.getMessage(), th.getClass().getSimpleName());
    }

}
