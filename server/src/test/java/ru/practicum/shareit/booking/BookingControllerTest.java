package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingCreationRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.repository.model.Booking.Status;
import ru.practicum.shareit.booking.service.BookingGetRequest;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.utills.UserHttpHeaders;

import java.time.LocalDateTime;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    @InjectMocks
    private BookingController controller;
    @Mock
    private BookingService bookingService;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void addBookingTest() throws Exception {

        final long userId = 1L;
        BookingCreationRequestDto requestDto = BookingCreationRequestDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .build();

        BookingDto dto = BookingDto.builder()
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .id(1L)
                .status(Status.WAITING)
                .build();

        when(bookingService.addBooking(any(), eq(userId)))
                .thenReturn(dto);

        mvc.perform(post("/bookings")
                        .header(UserHttpHeaders.USER_ID, userId)
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.status", equalTo(dto.getStatus().toString())));

    }

    @Test
    void updateBookingTest() throws Exception {

        final long userId = 1L;
        final Boolean approved = true;
        BookingCreationRequestDto requestDto = BookingCreationRequestDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .build();

        BookingDto dto = BookingDto.builder()
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .id(1L)
                .status(Status.APPROVED)
                .build();

        when(bookingService.updateBookingStatus(any(), eq(approved), eq(userId)))
                .thenReturn(dto);

        mvc.perform(patch("/bookings/1")
                        .header(UserHttpHeaders.USER_ID, userId)
                        .param("approved", approved.toString())
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.status", equalTo(dto.getStatus().toString())));

    }


    @Test
    void getBookingTest() throws Exception {

        final long userId = 1L;
        final long bookingId = 1L;
        BookingDto dto = BookingDto.builder()
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .id(1L)
                .status(Status.APPROVED)
                .build();

        when(bookingService.getBooking(bookingId, userId))
                .thenReturn(dto);

        mvc.perform(get("/bookings/1")
                        .header(UserHttpHeaders.USER_ID, userId)
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.status", equalTo(dto.getStatus().toString())));

    }

    @Test
    void getAllByBookerIdTest() throws Exception {

        final Integer from = 0;
        final Integer size = 10;
        final String state = "ALL";
        final long userId = 1L;
        final long bookingId = 1L;

        BookingDto dto = BookingDto.builder()
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .id(1L)
                .status(Status.APPROVED)
                .build();

        List<BookingDto> bookingDtos = List.of(dto);
        BookingGetRequest request = BookingGetRequest.builder()
                .userId(bookingId)
                .possibleState(state)
                .from(from)
                .size(size).build();

        when(bookingService.getAllByBookerId(request))
                .thenReturn(bookingDtos);

        mvc.perform(get("/bookings")
                        .header(UserHttpHeaders.USER_ID, userId)
                        .param("state", state)
                        .param("from", from.toString())
                        .param("size", size.toString())
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

    }

    @Test
    void getAllByBookerItemsTest() throws Exception {

        final Integer from = 0;
        final Integer size = 10;
        final String state = "ALL";
        final long userId = 1L;
        final long bookingId = 1L;

        BookingDto dto = BookingDto.builder()
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .id(1L)
                .status(Status.APPROVED)
                .build();

        List<BookingDto> bookingDtos = List.of(dto);
        BookingGetRequest request = BookingGetRequest.builder()
                .userId(bookingId)
                .possibleState(state)
                .from(from)
                .size(size).build();
        when(bookingService.getAllByBookerItems(request))
                .thenReturn(bookingDtos);

        mvc.perform(get("/bookings/owner")
                        .header(UserHttpHeaders.USER_ID, userId)
                        .param("state", state)
                        .param("from", from.toString())
                        .param("size", size.toString())
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

    }

}
