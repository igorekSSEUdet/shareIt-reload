package ru.practicum.shareit.user.exception;

import ru.practicum.shareit.exception.EntityNotFoundException;

public class UserNotFoundException extends EntityNotFoundException {
    private static final String USER_NOT_FOUND = "User ID_%d not found";

    public UserNotFoundException(String message) {
        super(message);
    }

    public static UserNotFoundException getFromUserId(Long userId) {
        return new UserNotFoundException(String.format(USER_NOT_FOUND, userId));
    }
}
