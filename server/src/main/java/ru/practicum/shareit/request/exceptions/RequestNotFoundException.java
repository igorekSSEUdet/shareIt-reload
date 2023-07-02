package ru.practicum.shareit.request.exceptions;

import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.exception.UserNotFoundException;

public class RequestNotFoundException extends EntityNotFoundException {

    private static final String REQUEST_NOT_FOUND = "Request ID_%d not found";

    public RequestNotFoundException(String message) {
        super(message);
    }

    public static UserNotFoundException getFromRequestId(Long requestId) {
        return new UserNotFoundException(String.format(REQUEST_NOT_FOUND, requestId));
    }
}
