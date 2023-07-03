package ru.practicum.shareit.booking.exception;

import ru.practicum.shareit.exception.LogicException;

public class BookingLogicException extends LogicException {
    private static final String USER_CANT_BOOK_OWN_ITEM = "User ID_%d cannot book own item ID_%d";

    public BookingLogicException(String message) {
        super(message);
    }

    public static BookingLogicException getFromOwnerIdAndItemId(Long ownerId, Long itemId) {
        return new BookingLogicException(String.format(USER_CANT_BOOK_OWN_ITEM, ownerId, itemId));
    }
}
