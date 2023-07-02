package ru.practicum.shareit.booking.exception;

import ru.practicum.shareit.exception.IncorrectDataException;

import static java.lang.String.format;

public class BookingAlreadyApprovedException extends IncorrectDataException {
    private static final String BOOKING_ALREADY_APPROVED = "Booking ID_%d already approved";

    public BookingAlreadyApprovedException(String message) {
        super(message);
    }

    public static BookingAlreadyApprovedException getFromBookingId(Long bookingId) {
        return new BookingAlreadyApprovedException(format(BOOKING_ALREADY_APPROVED, bookingId));
    }
}
