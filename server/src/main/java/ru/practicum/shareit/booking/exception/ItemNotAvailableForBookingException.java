package ru.practicum.shareit.booking.exception;

import ru.practicum.shareit.exception.IncorrectDataException;

import static java.lang.String.format;

public class ItemNotAvailableForBookingException extends IncorrectDataException {
    private static final String ITEM_NOT_AVAILABLE = "Item ID_%d is not available for booking";

    public ItemNotAvailableForBookingException(String message) {
        super(message);
    }

    public static ItemNotAvailableForBookingException getFromItemId(Long itemId) {
        throw new ItemNotAvailableForBookingException(format(ITEM_NOT_AVAILABLE, itemId));
    }
}
