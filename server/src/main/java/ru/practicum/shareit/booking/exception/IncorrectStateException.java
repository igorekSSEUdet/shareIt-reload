package ru.practicum.shareit.booking.exception;

import ru.practicum.shareit.exception.IncorrectDataException;

import static java.lang.String.format;

public class IncorrectStateException extends IncorrectDataException {
    private static final String UNKNOWN_STATE = "Unknown state: %s";

    public IncorrectStateException(String message) {
        super(message);
    }

    public static IncorrectStateException getFromIncorrectState(String state) {
        throw new IncorrectStateException(format(UNKNOWN_STATE, state));
    }
}
