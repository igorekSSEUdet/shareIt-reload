package ru.practicum.shareit.booking.exception;

import ru.practicum.shareit.exception.IncorrectDataException;

import java.time.LocalDateTime;

import static java.lang.String.format;

public class IncorrectBookingDatesException extends IncorrectDataException {
    private static final String INCORRECT_DATES = "End date must be after the start date: %s <--> %s";

    public IncorrectBookingDatesException(String message) {
        super(message);
    }

    public static IncorrectBookingDatesException getFromDates(LocalDateTime start, LocalDateTime end) {
        return new IncorrectBookingDatesException(format(INCORRECT_DATES, start, end));
    }
}
