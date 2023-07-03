package ru.practicum.shareit.item.exception;

import ru.practicum.shareit.exception.EntityNotFoundException;

import static java.lang.String.format;

public class ItemNotFoundException extends EntityNotFoundException {
    private static final String ITEM_NOT_FOUND = "Item ID_%d not found";
    private static final String ITEM_WITH_OWNER_NOT_FOUND = "Item ID_%d with owner ID_%d not found";

    public ItemNotFoundException(String message) {
        super(message);
    }

    public static ItemNotFoundException getFromItemId(Long itemId) {
        return new ItemNotFoundException(format(ITEM_NOT_FOUND, itemId));
    }

    public static ItemNotFoundException getFromItemIdAndUserId(Long itemId, Long ownerId) {
        return new ItemNotFoundException(format(ITEM_WITH_OWNER_NOT_FOUND, itemId, ownerId));
    }
}
