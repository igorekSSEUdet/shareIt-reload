package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreationRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingGetRequest;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.utills.UserHttpHeaders;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(@RequestHeader(UserHttpHeaders.USER_ID) Long userId,
                                 @RequestBody @Valid BookingCreationRequestDto bookingDto) {
        log.info("Received a POST request for the endpoint /bookings with userId_{}", userId);
        return bookingService.addBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBookingStatus(@RequestHeader(UserHttpHeaders.USER_ID) Long userId,
                                          @PathVariable Long bookingId,
                                          @RequestParam Boolean approved) {
        log.info("Received a PATCH request for the endpoint /bookings/bookingId with userId_{}", userId);
        return bookingService.updateBookingStatus(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader(UserHttpHeaders.USER_ID) Long userId,
                                 @PathVariable Long bookingId) {
        log.info("Received a GET request for the endpoint /bookings/bookingId with userId_{}", userId);
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getAllByBookerId(@RequestHeader(UserHttpHeaders.USER_ID) Long bookerId,
                                             @RequestParam String state,
                                             @RequestParam(required = false) Integer from,
                                             @RequestParam(required = false) Integer size) {
        log.info("Received a GET request for the endpoint /bookings with userId_{}", bookerId);
        BookingGetRequest request = BookingGetRequest.builder()
                .userId(bookerId).possibleState(state).from(from).size(size).build();
        return bookingService.getAllByBookerId(request);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllByBookerItems(@RequestHeader(UserHttpHeaders.USER_ID) Long ownerId,
                                                @RequestParam String state,
                                                @RequestParam(required = false) Integer from,
                                                @RequestParam(required = false) Integer size) {
        log.info("Received a GET request for the endpoint /bookings/owner with userId_{}", ownerId);
        BookingGetRequest request = BookingGetRequest.builder()
                .userId(ownerId).possibleState(state).from(from).size(size).build();
        return bookingService.getAllByBookerItems(request);
    }
}
