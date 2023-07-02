package ru.practicum.shareit.booking.exception;

import ru.practicum.shareit.exception.EntityNotFoundException;

import static java.lang.String.format;

public class BookingNotFoundException extends EntityNotFoundException {
    private static final String BOOKING_NOT_FOUND = "Booking ID_%d not found";
    private static final String BOOKING_WITH_OWNER_OR_BOOKER_NOT_FOUND =
            "Booking ID_%d with owner or booker ID_%d not found";

    public BookingNotFoundException(String message) {
        super(message);
    }

    public static BookingNotFoundException getFromBookingId(Long bookingId) {
        return new BookingNotFoundException(format(BOOKING_NOT_FOUND, bookingId));
    }

    public static BookingNotFoundException getFromBookingIdAndUserId(Long bookingId, Long userId) {
        return new BookingNotFoundException(format(
                BOOKING_WITH_OWNER_OR_BOOKER_NOT_FOUND, bookingId, userId));
    }
}
