package com.example.gateway.booking.service;

import com.example.gateway.booking.dto.BookingCreationRequestDto;
import org.springframework.http.ResponseEntity;

public interface BookingRemoteCommand {

    ResponseEntity<Object> addBooking(Long userId, BookingCreationRequestDto bookingDto);

    ResponseEntity<Object> updateBookingStatus(Long userId, Long bookingId, Boolean approved);

    ResponseEntity<Object> getBooking(Long userId, Long bookingId);

    ResponseEntity<Object> getAllByBookerId(Long bookerId, String state, Integer from, Integer size);

    ResponseEntity<Object> getAllByBookerItems(Long ownerId, String state, Integer from, Integer size);

}
