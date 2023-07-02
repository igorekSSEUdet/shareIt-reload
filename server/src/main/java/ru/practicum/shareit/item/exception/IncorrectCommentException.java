package ru.practicum.shareit.item.exception;

import ru.practicum.shareit.exception.IncorrectDataException;

import java.time.LocalDateTime;

import static java.lang.String.format;

public class IncorrectCommentException extends IncorrectDataException {
    private static final String COMPLETED_BOOKING_NOT_FOUND;

    static {
        COMPLETED_BOOKING_NOT_FOUND = "No completed user ID_%d booking item ID_%d found at the moment %s";
    }

    public IncorrectCommentException(String message) {
        super(message);
    }

    public static IncorrectCommentException getFromUserIdAndItemIdAndTime(Long userId,
                                                                          Long itemId,
                                                                          LocalDateTime time) {
        return new IncorrectCommentException(format(COMPLETED_BOOKING_NOT_FOUND, userId, itemId, time));
    }
}
