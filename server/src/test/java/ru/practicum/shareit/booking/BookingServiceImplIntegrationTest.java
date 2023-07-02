package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingCreationRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingGetRequest;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.dto.ItemCreationRequestDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserCreationRequestDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.booking.repository.model.Booking.Status.APPROVED;
import static ru.practicum.shareit.booking.repository.model.Booking.Status.WAITING;

@SpringBootTest
public class BookingServiceImplIntegrationTest {

    private final ItemCreationRequestDto item = ItemCreationRequestDto.builder().available(true).name("name")
            .description("desc").build();
    private final UserCreationRequestDto user = UserCreationRequestDto.builder().name("name").email("mail@mail.ru").build();
    private final BookingCreationRequestDto booking = BookingCreationRequestDto.builder().itemId(1L)
            .start(now().plusDays(1)).end(now().plusDays(2)).build();
    @Autowired
    UserServiceImpl userService;
    @Autowired
    BookingServiceImpl bookingService;
    @Autowired
    private ItemServiceImpl itemService;

    public BookingServiceImplIntegrationTest() {
    }

    @Test
    @DirtiesContext
    public void addBookingTest() {
        final UserCreationRequestDto user1 = UserCreationRequestDto.builder().name("name").email("mail@google.com").build();

        userService.addUser(user);
        userService.addUser(user1);
        itemService.addItem(item, 1L);

        BookingDto addedBooking = bookingService.addBooking(booking, 2L);

        assertEquals(addedBooking.getId(), 1L);
        assertEquals(addedBooking.getStart(), booking.getStart());
        assertEquals(addedBooking.getEnd(), booking.getEnd());
        assertEquals(addedBooking.getStatus(), WAITING);

    }

    @Test
    @DirtiesContext
    public void updateBookingStatusTest() {
        final UserCreationRequestDto user1 = UserCreationRequestDto.builder().name("name").email("mail@google.com").build();

        userService.addUser(user);
        userService.addUser(user1);
        itemService.addItem(item, 1L);
        bookingService.addBooking(booking, 2L);
        BookingDto updatedBooking = bookingService.updateBookingStatus(1L, true, 1L);
        System.out.println(updatedBooking);

        assertEquals(updatedBooking.getId(), 1L);
        assertEquals(updatedBooking.getStatus(), APPROVED);

    }

    @Test
    @DirtiesContext
    public void getItemTest() {
        final UserCreationRequestDto user1 = UserCreationRequestDto.builder().name("name").email("mail@google.com").build();

        userService.addUser(user);
        userService.addUser(user1);
        itemService.addItem(item, 1L);
        bookingService.addBooking(booking, 2L);
        BookingDto addedBooking = bookingService.getBooking(1L, 1L);

        assertEquals(addedBooking.getId(), 1L);
        assertEquals(addedBooking.getStatus(), WAITING);

    }

    @Test
    @DirtiesContext
    public void getAllByBookerIdTest() {
        final UserCreationRequestDto user1 = UserCreationRequestDto.builder().name("name").email("mail@google.com").build();

        userService.addUser(user);
        userService.addUser(user1);
        itemService.addItem(item, 1L);
        bookingService.addBooking(booking, 2L);

        final BookingGetRequest request = BookingGetRequest.builder().possibleState("FUTURE").userId(2L).build();
        List<BookingDto> result = bookingService.getAllByBookerId(request);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getId(), 1L);
        assertEquals(result.get(0).getStatus(), WAITING);


    }
}
