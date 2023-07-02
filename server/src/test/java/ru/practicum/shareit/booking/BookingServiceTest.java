package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingCreationRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.booking.mapper.BookingDtoMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.repository.model.Booking;
import ru.practicum.shareit.booking.service.BookingGetRequest;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.mapper.ItemDtoMapper;
import ru.practicum.shareit.user.mapper.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.repository.model.Booking.Status.REJECTED;
import static ru.practicum.shareit.booking.repository.model.Booking.Status.WAITING;
import static ru.practicum.shareit.booking.repository.model.Booking.Status.*;
import static ru.practicum.shareit.booking.repository.model.Booking.builder;
import static ru.practicum.shareit.booking.service.State.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingDtoMapper bookingDtoMapper;
    @Mock
    private UserDtoMapper userDtoMapper;
    @Mock
    private ItemDtoMapper itemDtoMapper;
    private BookingService bookingService;

    @BeforeEach
    public void setUp() {
        this.bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository, bookingDtoMapper, userDtoMapper, itemDtoMapper);
    }


    @Test
    public void testAddBooking_Success() {
        BookingCreationRequestDto bookingDto = new BookingCreationRequestDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(now().plusDays(1));
        bookingDto.setEnd(now().plusDays(2));

        User user = new User();
        user.setId(1L);

        Item item = new Item();
        item.setId(bookingDto.getItemId());
        item.setAvailable(true);
        item.setOwner(user);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStartTime(bookingDto.getStart());
        booking.setEndTime(bookingDto.getEnd());
        booking.setBooker(user);
        booking.setItem(item);

        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(itemRepository.existsById(item.getId())).thenReturn(true);
        when(itemRepository.getReferenceById(anyLong())).thenReturn(Item.builder().owner(User.builder().id(2L).build()).build());
        when(bookingDtoMapper.toBooking(any(BookingCreationRequestDto.class), eq(user.getId()))).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingDtoMapper.toBookingDto(any(Booking.class), eq(userDtoMapper), eq(itemDtoMapper))).thenReturn(new BookingDto());

        BookingDto result = bookingService.addBooking(bookingDto, user.getId());

        assertNotNull(result);
        verify(userRepository).existsById(user.getId());
        verify(itemRepository).existsById(item.getId());
        verify(bookingDtoMapper).toBooking(any(BookingCreationRequestDto.class), eq(user.getId()));
        verify(bookingRepository).save(booking);
        verify(bookingDtoMapper).toBookingDto(any(Booking.class), eq(userDtoMapper), eq(itemDtoMapper));
    }

    @Test
    public void testAddBooking_OwnerIdEqualsBookerId() {
        BookingCreationRequestDto bookingDto = new BookingCreationRequestDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(now().plusDays(1));
        bookingDto.setEnd(now().plusDays(2));

        User user = new User();
        user.setId(1L);

        Item item = new Item();
        item.setId(bookingDto.getItemId());
        item.setAvailable(true);
        item.setOwner(user);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStartTime(bookingDto.getStart());
        booking.setEndTime(bookingDto.getEnd());
        booking.setBooker(user);
        booking.setItem(item);

        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(itemRepository.existsById(item.getId())).thenReturn(true);
        when(itemRepository.getReferenceById(anyLong())).thenReturn(item);

        assertThrows(BookingLogicException.class, () ->
                bookingService.addBooking(bookingDto, user.getId()));

    }

    @Test
    public void testUpdateBookingStatus() {
        when(bookingRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.existsById(anyLong())).thenReturn(true);

        User user = User.builder().id(1L).build();

        Item item = Item.builder().id(1L).owner(user).build();

        Booking booking = new Booking();
        booking.setStatus(WAITING);
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStartTime(now().plusDays(2));
        booking.setEndTime(now().plusDays(3));

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(itemRepository.getReferenceById(item.getId())).thenReturn(item);

        when(bookingDtoMapper.toBookingDto(booking, userDtoMapper, itemDtoMapper))
                .thenReturn(BookingDto.builder()
                        .start(booking.getStartTime())
                        .end(booking.getEndTime())
                        .status(booking.getStatus()).build());

        BookingDto bookingDto = bookingService.updateBookingStatus(1L, true, 1L);
        System.out.println(bookingDto);
        assertEquals(APPROVED, booking.getStatus());
    }

    @Test
    public void testUpdateBookingStatusBookingNotExists() {
        when(bookingRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(BookingNotFoundException.class, () ->
                bookingService.updateBookingStatus(1L, Boolean.TRUE, 1L));
    }

    @Test
    public void testUpdateBookingStatuUserNotExists() {
        when(bookingRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(UserNotFoundException.class, () ->
                bookingService.updateBookingStatus(1L, Boolean.TRUE, 1L));
    }

    @Test
    public void testUpdateBookingStatusNotApprove() {
        when(bookingRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.existsById(anyLong())).thenReturn(true);

        User user = User.builder().id(1L).build();

        Item item = Item.builder().id(1L).owner(user).build();

        Booking booking = new Booking();
        booking.setStatus(APPROVED);
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStartTime(now().plusDays(2));
        booking.setEndTime(now().plusDays(3));

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(itemRepository.getReferenceById(item.getId())).thenReturn(item);

        assertThrows(BookingAlreadyApprovedException.class, () ->
                bookingService.updateBookingStatus(1L, Boolean.TRUE, 1L));
    }

    @Test
    public void testGetBooking() {

        User user = new User();
        user.setId(1L);
        Item item = new Item();
        item.setOwner(user);
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);
        when(bookingRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingDtoMapper.toBookingDto(booking, userDtoMapper, itemDtoMapper))
                .thenReturn(BookingDto.builder().id(booking.getId()).build());

        BookingDto bookingDto = bookingService.getBooking(1L, 1L);

        assertEquals(1L, bookingDto.getId());
    }

    @Test
    public void testGetBookingUserNotOwnerOrBooker() {

        User user = new User();
        user.setId(1L);
        Item item = new Item();
        item.setOwner(user);
        Booking booking = new Booking();
        booking.setId(2L);
        booking.setBooker(user);
        booking.setItem(item);
        when(bookingRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(BookingNotFoundException.class, () -> bookingService.getBooking(1L, 2L));
    }


    @Test
    public void testGetAllBookingsByBookerId() {

        Booking booking = builder().id(1L).build();

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findBookingByBookerIdOrderByStartTimeDesc(anyLong()))
                .thenReturn(List.of(booking));
        when(bookingDtoMapper.toBookingDto(List.of(booking), userDtoMapper, itemDtoMapper))
                .thenReturn(List.of(BookingDto.builder().id(1L).build()));
        BookingGetRequest request = BookingGetRequest.builder()
                .userId(1L)
                .possibleState("ALL")
                .from(null)
                .size(null).build();
        List<BookingDto> bookingDto = bookingService.getAllByBookerId(request);

        assertEquals(bookingDto.get(0).getId(), 1L);

    }

    @Test
    public void testGetAllBookingsByBookerIdStatusWaiting() {

        Booking booking = builder().id(1L).build();

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findBookingsByBookerIdAndStatusEquals(anyLong(), eq(WAITING)))
                .thenReturn(List.of(booking));
        when(bookingDtoMapper.toBookingDto(List.of(booking), userDtoMapper, itemDtoMapper))
                .thenReturn(List.of(BookingDto.builder().id(1L).build()));
        BookingGetRequest request = BookingGetRequest.builder()
                .userId(1L)
                .possibleState("WAITING")
                .from(null)
                .size(null).build();
        List<BookingDto> bookingDto = bookingService.getAllByBookerId(request);

        assertEquals(bookingDto.get(0).getId(), 1L);

    }

    @Test
    public void testGetAllBookingsByBookerIdStatusRejected() {

        Booking booking = builder().id(1L).build();

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findBookingsByBookerIdAndStatusEquals(anyLong(), eq(REJECTED)))
                .thenReturn(List.of(booking));
        when(bookingDtoMapper.toBookingDto(List.of(booking), userDtoMapper, itemDtoMapper))
                .thenReturn(List.of(BookingDto.builder().id(1L).build()));
        BookingGetRequest request = BookingGetRequest.builder()
                .userId(1L)
                .possibleState("REJECTED")
                .from(null)
                .size(null).build();

        List<BookingDto> bookingDto = bookingService.getAllByBookerId(request);

        assertEquals(bookingDto.get(0).getId(), 1L);

    }

    @Test
    public void testGetAllBookingsByBookerIdWithPagination() {

        Booking booking = builder().id(1L).build();

        int from = 0;
        int size = 5;

        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(bookingRepository.findBookingByBookerIdOrderByStartTimeDesc(anyLong(),
                eq(PageRequest.of(from, size))))
                .thenReturn(new PageImpl<>(List.of(booking)));

        when(bookingDtoMapper.toBookingDto(List.of(booking), userDtoMapper, itemDtoMapper))
                .thenReturn(List.of(BookingDto.builder().id(1L).build()));
        BookingGetRequest request = BookingGetRequest.builder()
                .userId(1L)
                .possibleState("ALL")
                .from(from)
                .size(size).build();

        List<BookingDto> bookingDto = bookingService.getAllByBookerId(request);

        assertEquals(bookingDto.get(0).getId(), 1L);

    }

    @Test
    public void testGetAllBookingsByBookerIdStatusWaitingWithPagination() {

        Booking booking = builder().id(1L).build();

        int from = 0;
        int size = 5;

        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(bookingRepository.findBookingsByBookerIdAndStatusEquals(anyLong(), eq(WAITING), eq(PageRequest.of(from, size))))
                .thenReturn(new PageImpl<>(List.of(booking)));

        when(bookingDtoMapper.toBookingDto(List.of(booking), userDtoMapper, itemDtoMapper))
                .thenReturn(List.of(BookingDto.builder().id(1L).build()));

        BookingGetRequest request = BookingGetRequest.builder()
                .userId(1L)
                .possibleState("WAITING")
                .from(from)
                .size(size).build();

        List<BookingDto> bookingDto = bookingService.getAllByBookerId(request);

        assertEquals(bookingDto.get(0).getId(), 1L);

    }

    @Test
    public void testGetAllBookingsByBookerIdStatusRejectedWithPagination() {

        Booking booking = builder().id(1L).build();

        int from = 0;
        int size = 5;

        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(bookingRepository.findBookingsByBookerIdAndStatusEquals(anyLong(), eq(REJECTED), eq(PageRequest.of(from, size))))
                .thenReturn(new PageImpl<>(List.of(booking)));

        when(bookingDtoMapper.toBookingDto(List.of(booking), userDtoMapper, itemDtoMapper))
                .thenReturn(List.of(BookingDto.builder().id(1L).build()));
        BookingGetRequest request = BookingGetRequest.builder()
                .userId(1L)
                .possibleState("REJECTED")
                .from(from)
                .size(size).build();

        List<BookingDto> bookingDto = bookingService.getAllByBookerId(request);

        assertEquals(bookingDto.get(0).getId(), 1L);

    }

    @Test
    public void testGetAllBookingsByBookerIdForOwner() {

        Booking booking = builder().id(1L).build();

        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(bookingRepository.findBookingsByItemOwnerIdOrderByStartTimeDesc(anyLong()))
                .thenReturn(List.of(booking));

        when(bookingDtoMapper.toBookingDto(List.of(booking), userDtoMapper, itemDtoMapper))
                .thenReturn(List.of(BookingDto.builder().id(booking.getId()).build()));

        BookingGetRequest request = BookingGetRequest.builder()
                .userId(1L)
                .possibleState("ALL")
                .from(null)
                .size(null).build();

        List<BookingDto> bookingDto = bookingService.getAllByBookerItems(request);

        assertEquals(bookingDto.get(0).getId(), 1L);

    }

    @Test
    public void testGetAllBookingsByBookerIdStatusWaitingForOwner() {

        Booking booking = builder().id(1L).build();

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findBookingsByItemOwnerIdAndStatusEquals(anyLong(), eq(WAITING)))
                .thenReturn(List.of(booking));
        when(bookingDtoMapper.toBookingDto(List.of(booking), userDtoMapper, itemDtoMapper))
                .thenReturn(List.of(BookingDto.builder().id(1L).build()));

        BookingGetRequest request = BookingGetRequest.builder()
                .userId(1L)
                .possibleState("WAITING")
                .from(null)
                .size(null).build();

        List<BookingDto> bookingDto = bookingService.getAllByBookerItems(request);

        assertEquals(bookingDto.get(0).getId(), 1L);

    }

    @Test
    public void testGetAllBookingsByBookerIdStatusRejectedForOwner() {

        Booking booking = builder().id(1L).build();

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findBookingsByItemOwnerIdAndStatusEquals(anyLong(), eq(REJECTED)))
                .thenReturn(List.of(booking));
        when(bookingDtoMapper.toBookingDto(List.of(booking), userDtoMapper, itemDtoMapper))
                .thenReturn(List.of(BookingDto.builder().id(1L).build()));
        BookingGetRequest request = BookingGetRequest.builder()
                .userId(1L)
                .possibleState("REJECTED")
                .from(null)
                .size(null).build();
        List<BookingDto> bookingDto = bookingService.getAllByBookerItems(request);

        assertEquals(bookingDto.get(0).getId(), 1L);

    }

    @Test
    public void testGetAllBookingsByBookerIdWithPaginationForOwner() {

        Booking booking = builder().id(1L).build();

        int from = 0;
        int size = 5;

        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(bookingRepository.findBookingsByItemOwnerIdOrderByStartTimeAsc(anyLong(),
                eq(PageRequest.of(from, size))))
                .thenReturn(new PageImpl<>(List.of(booking)));

        when(bookingDtoMapper.toBookingDto(List.of(booking), userDtoMapper, itemDtoMapper))
                .thenReturn(List.of(BookingDto.builder().id(1L).build()));

        BookingGetRequest request = BookingGetRequest.builder()
                .userId(1L)
                .possibleState("ALL")
                .from(from)
                .size(size).build();

        List<BookingDto> bookingDto = bookingService.getAllByBookerItems(request);

        assertEquals(bookingDto.get(0).getId(), 1L);

    }

    @Test
    public void testGetAllBookingsByBookerIdStatusWaitingWithPaginationForOwner() {

        Booking booking = builder().id(1L).build();

        int from = 0;
        int size = 5;

        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(bookingRepository.findBookingsByItemOwnerIdAndStatusEquals(anyLong(), eq(WAITING), eq(PageRequest.of(from, size))))
                .thenReturn(new PageImpl<>(List.of(booking)));

        when(bookingDtoMapper.toBookingDto(List.of(booking), userDtoMapper, itemDtoMapper))
                .thenReturn(List.of(BookingDto.builder().id(1L).build()));

        BookingGetRequest request = BookingGetRequest.builder()
                .userId(1L)
                .possibleState("WAITING")
                .from(from)
                .size(size).build();

        List<BookingDto> bookingDto = bookingService.getAllByBookerItems(request);

        assertEquals(bookingDto.get(0).getId(), 1L);

    }

    @Test
    public void testGetAllBookingsByBookerIdStatusRejectedWithPaginationForOwner() {

        Booking booking = builder().id(1L).build();

        int from = 0;
        int size = 5;

        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(bookingRepository.findBookingsByItemOwnerIdAndStatusEquals(anyLong(), eq(REJECTED), eq(PageRequest.of(from, size))))
                .thenReturn(new PageImpl<>(List.of(booking)));

        when(bookingDtoMapper.toBookingDto(List.of(booking), userDtoMapper, itemDtoMapper))
                .thenReturn(List.of(BookingDto.builder().id(1L).build()));

        BookingGetRequest request = BookingGetRequest.builder()
                .userId(1L)
                .possibleState("REJECTED")
                .from(from)
                .size(size).build();

        List<BookingDto> bookingDto = bookingService.getAllByBookerItems(request);

        assertEquals(bookingDto.get(0).getId(), 1L);

    }

    @Test
    public void testCheckItemExistsById_ItemNotFoundException() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.existsById(anyLong())).thenReturn(false);
        BookingCreationRequestDto dto = BookingCreationRequestDto.builder().itemId(1L).build();
        assertThrows(ItemNotFoundException.class, () -> bookingService.addBooking(dto, 1L));

        verify(itemRepository, times(1)).existsById(anyLong());
    }

    @Test
    public void addBookingUserNotFoundTest() {
        when(userRepository.existsById(anyLong())).thenReturn(false);
        BookingCreationRequestDto dto = BookingCreationRequestDto.builder().itemId(1L).build();
        assertThrows(UserNotFoundException.class, () -> bookingService.addBooking(dto, 1L));

        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    public void addBookingStartTimeIsAfterEndTimeShouldThrowIncorrectBookingDatesException() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.existsById(anyLong())).thenReturn(true);

        when(itemRepository.getReferenceById(anyLong())).thenReturn(Item.builder()
                .owner(User.builder().id(2L).build()).build());

        BookingCreationRequestDto dtoRequest = BookingCreationRequestDto.builder()
                .itemId(2L)
                .start(now().plusDays(5))
                .end(now())
                .build();

        assertThrows(IncorrectBookingDatesException.class, () -> bookingService.addBooking(dtoRequest, 1L));


    }

    @Test
    public void addBookingStartTimeIsEqualsEndTimeShouldThrowIncorrectBookingDatesException() {

        LocalDateTime dateTime = now();
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.existsById(anyLong())).thenReturn(true);

        when(itemRepository.getReferenceById(anyLong())).thenReturn(Item.builder()
                .owner(User.builder().id(2L).build()).build());

        BookingCreationRequestDto dtoRequest = BookingCreationRequestDto.builder()
                .itemId(2L)
                .start(dateTime)
                .end(dateTime)
                .build();

        assertThrows(IncorrectBookingDatesException.class, () -> bookingService.addBooking(dtoRequest, 1L));


    }

    @Test
    public void addBookingEndTimeIsBeforeStartTimeShouldThrowIncorrectBookingDatesException() {

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.existsById(anyLong())).thenReturn(true);

        when(itemRepository.getReferenceById(anyLong())).thenReturn(Item.builder()
                .owner(User.builder().id(2L).build()).build());

        BookingCreationRequestDto dtoRequest = BookingCreationRequestDto.builder()
                .itemId(2L)
                .start(now().plusDays(5))
                .end(now())
                .build();

        assertThrows(IncorrectBookingDatesException.class, () -> bookingService.addBooking(dtoRequest, 1L));


    }

    @Test
    public void addBookingItemAvailableIsFalse() {

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.existsById(anyLong())).thenReturn(true);

        when(itemRepository.getReferenceById(anyLong())).thenReturn(Item.builder()
                .owner(User.builder().id(2L).build()).build());


        BookingCreationRequestDto dtoRequest = BookingCreationRequestDto.builder()
                .itemId(2L)
                .start(now().minusDays(5))
                .end(now())
                .build();

        when(bookingDtoMapper.toBooking(dtoRequest, 1L))
                .thenReturn(Booking.builder().item(Item.builder().available(Boolean.FALSE).build()).build());

        assertThrows(ItemNotAvailableForBookingException.class, () -> bookingService.addBooking(dtoRequest, 1L));

    }

    @Test
    public void updateBookingStatusBookingNotFound() {

        when(bookingRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.updateBookingStatus(1L, true, 1L));

    }

    @Test
    public void updateBookingStatusUserNotFound() {

        when(bookingRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(UserNotFoundException.class,
                () -> bookingService.updateBookingStatus(1L, true, 1L));

    }

    @Test
    public void updateBookingUserNotOwner() {

        when(bookingRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.existsById(anyLong())).thenReturn(true);

        Booking booking = Booking.builder().id(1L).item(Item.builder().id(2L).build()).build();
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(itemRepository.getReferenceById(booking.getItem().getId())).thenReturn(Item.builder().owner(User.builder().id(2L).build()).build());

        assertThrows(ItemNotFoundException.class,
                () -> bookingService.updateBookingStatus(1L, true, 1L));

    }

    @Test
    public void updateBookingStatusAlreadyApproved() {

        when(bookingRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.existsById(anyLong())).thenReturn(true);

        Booking booking = Booking.builder().id(1L).status(APPROVED).item(Item.builder().id(2L).build()).build();
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(itemRepository.getReferenceById(booking.getItem().getId())).thenReturn(Item.builder().owner(User.builder().id(1L).build()).build());

        assertThrows(BookingAlreadyApprovedException.class,
                () -> bookingService.updateBookingStatus(1L, true, 1L));

    }

    @Test
    public void testGetAllBookingsByBookerIdStatusPast() {

        BookingGetRequest request = BookingGetRequest.builder()
                .size(null)
                .from(null)
                .userId(1L)
                .possibleState(String.valueOf(PAST)).build();

        when(userRepository.existsById(anyLong())).thenReturn(true);
        Booking booking = Booking.builder()
                .id(1L)
                .status(WAITING)
                .endTime(LocalDateTime.now().plusDays(5))
                .startTime(LocalDateTime.now().plusDays(3))
                .item(Item.builder().id(5L).available(Boolean.TRUE).description("desc").build())
                .booker(User.builder().id(1L).name("name").email("email@email.ru").build())
                .build();

        when(bookingRepository.findAllByBookerIdAndEndTimeIsBeforeOrderByStartTimeDesc(eq(request.getUserId()), any(LocalDateTime.class))).thenReturn(List.of(booking));

        when(bookingDtoMapper.toBookingDto(anyList(), eq(userDtoMapper), eq(itemDtoMapper))).thenReturn(
                List.of(BookingDto.builder().id(booking.getId()).status(booking.getStatus()).end(booking.getEndTime()).start(booking.getStartTime()).build())
        );

        List<BookingDto> result = bookingService.getAllByBookerId(request);

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getId(), 1L);
        assertEquals(result.get(0).getStart(), booking.getStartTime());
        assertEquals(result.get(0).getEnd(), booking.getEndTime());
        assertEquals(result.get(0).getStatus(), booking.getStatus());

        verify(userRepository, times(1)).existsById(anyLong());
        verify(userRepository, times(1)).existsById(anyLong());
        verify(bookingDtoMapper, times(1)).toBookingDto(anyList(), eq(userDtoMapper), eq(itemDtoMapper));
        verify(bookingRepository, times(1)).findAllByBookerIdAndEndTimeIsBeforeOrderByStartTimeDesc(
                anyLong(), any(LocalDateTime.class));

    }

    @Test
    public void testGetAllBookingsByBookerIdStatusFuture() {

        BookingGetRequest request = BookingGetRequest.builder()
                .size(null)
                .from(null)
                .userId(1L)
                .possibleState(String.valueOf(FUTURE)).build();

        when(userRepository.existsById(anyLong())).thenReturn(true);
        Booking booking = Booking.builder()
                .id(1L)
                .status(WAITING)
                .endTime(LocalDateTime.now().plusDays(5))
                .startTime(LocalDateTime.now().plusDays(3))
                .item(Item.builder().id(5L).available(Boolean.TRUE).description("desc").build())
                .booker(User.builder().id(1L).name("name").email("email@email.ru").build())
                .build();

        when(bookingRepository.findBookingsByBookerIdAndStartTimeIsAfterOrderByStartTimeDesc(eq(request.getUserId()), any(LocalDateTime.class))).thenReturn(List.of(booking));

        when(bookingDtoMapper.toBookingDto(anyList(), eq(userDtoMapper), eq(itemDtoMapper))).thenReturn(
                List.of(BookingDto.builder().id(booking.getId())
                        .status(booking.getStatus())
                        .end(booking.getEndTime())
                        .start(booking.getStartTime()).build())
        );

        List<BookingDto> result = bookingService.getAllByBookerId(request);

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getId(), 1L);
        assertEquals(result.get(0).getStart(), booking.getStartTime());
        assertEquals(result.get(0).getEnd(), booking.getEndTime());
        assertEquals(result.get(0).getStatus(), booking.getStatus());

        verify(userRepository, times(1)).existsById(anyLong());
        verify(userRepository, times(1)).existsById(anyLong());
        verify(bookingDtoMapper, times(1)).toBookingDto(anyList(), eq(userDtoMapper), eq(itemDtoMapper));
        verify(bookingRepository, times(1)).findBookingsByBookerIdAndStartTimeIsAfterOrderByStartTimeDesc(
                anyLong(), any(LocalDateTime.class));

    }

    @Test
    public void testGetAllBookingsByBookerIdStatusCurrent() {

        BookingGetRequest request = BookingGetRequest.builder()
                .size(null)
                .from(null)
                .userId(1L)
                .possibleState(String.valueOf(CURRENT)).build();

        when(userRepository.existsById(anyLong())).thenReturn(true);
        Booking booking = Booking.builder()
                .id(1L)
                .status(WAITING)
                .endTime(LocalDateTime.now().plusDays(5))
                .startTime(LocalDateTime.now().minusDays(3))
                .item(Item.builder().id(5L).available(Boolean.TRUE).description("desc").build())
                .booker(User.builder().id(1L).name("name").email("email@email.ru").build())
                .build();

        when(bookingRepository.findBookingByBookerIdAndStartTimeIsBeforeAndEndTimeIsAfter(eq(request.getUserId()), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(List.of(booking));

        when(bookingDtoMapper.toBookingDto(anyList(), eq(userDtoMapper), eq(itemDtoMapper))).thenReturn(
                List.of(BookingDto.builder().id(booking.getId())
                        .status(booking.getStatus())
                        .end(booking.getEndTime())
                        .start(booking.getStartTime()).build())
        );

        List<BookingDto> result = bookingService.getAllByBookerId(request);

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getId(), 1L);
        assertEquals(result.get(0).getStart(), booking.getStartTime());
        assertEquals(result.get(0).getEnd(), booking.getEndTime());
        assertEquals(result.get(0).getStatus(), booking.getStatus());

        verify(userRepository, times(1)).existsById(anyLong());
        verify(userRepository, times(1)).existsById(anyLong());
        verify(bookingDtoMapper, times(1)).toBookingDto(anyList(), eq(userDtoMapper), eq(itemDtoMapper));
        verify(bookingRepository, times(1)).findBookingByBookerIdAndStartTimeIsBeforeAndEndTimeIsAfter(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class));

    }

    @Test
    public void testGetAllBookingsByBookerIdStatusFutureWithPagination() {

        Booking booking = builder().id(1L).status(WAITING)
                .startTime(LocalDateTime.now().plusDays(3))
                .endTime(LocalDateTime.now().plusDays(5)).build();

        int from = 0;
        int size = 5;

        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(bookingRepository.findBookingsByBookerIdAndStartTimeIsAfterOrderByStartTimeDesc(anyLong(),
                any(LocalDateTime.class), eq(PageRequest.of(from, size))))
                .thenReturn(new PageImpl<>(List.of(booking)));

        when(bookingDtoMapper.toBookingDto(List.of(booking), userDtoMapper, itemDtoMapper))
                .thenReturn(List.of(BookingDto.builder().id(1L)
                        .status(booking.getStatus())
                        .start(booking.getStartTime())
                        .end(booking.getEndTime())
                        .build()));
        BookingGetRequest request = BookingGetRequest.builder()
                .userId(1L)
                .possibleState(String.valueOf(FUTURE))
                .from(from)
                .size(size).build();

        List<BookingDto> bookingDto = bookingService.getAllByBookerId(request);

        assertEquals(bookingDto.get(0).getId(), 1L);
        assertEquals(bookingDto.get(0).getStatus(), booking.getStatus());
        assertEquals(bookingDto.get(0).getId(), booking.getId());
        assertEquals(bookingDto.get(0).getStart(), booking.getStartTime());
        assertEquals(bookingDto.get(0).getEnd(), booking.getEndTime());

    }

    @Test
    public void testGetAllBookingsByBookerIdStatusCurrentWithPagination() {

        Booking booking = builder().id(1L).status(WAITING)
                .startTime(LocalDateTime.now().plusDays(3))
                .endTime(LocalDateTime.now().plusDays(5)).build();

        int from = 0;
        int size = 5;

        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(bookingRepository.findBookingByBookerIdAndStartTimeIsBeforeAndEndTimeIsAfter(anyLong(),
                any(LocalDateTime.class), any(LocalDateTime.class), eq(PageRequest.of(from, size))))
                .thenReturn(new PageImpl<>(List.of(booking)));

        when(bookingDtoMapper.toBookingDto(List.of(booking), userDtoMapper, itemDtoMapper))
                .thenReturn(List.of(BookingDto.builder().id(1L)
                        .status(booking.getStatus())
                        .start(booking.getStartTime())
                        .end(booking.getEndTime())
                        .build()));
        BookingGetRequest request = BookingGetRequest.builder()
                .userId(1L)
                .possibleState(String.valueOf(CURRENT))
                .from(from)
                .size(size).build();

        List<BookingDto> bookingDto = bookingService.getAllByBookerId(request);

        assertEquals(bookingDto.get(0).getId(), 1L);
        assertEquals(bookingDto.get(0).getStatus(), booking.getStatus());
        assertEquals(bookingDto.get(0).getId(), booking.getId());
        assertEquals(bookingDto.get(0).getStart(), booking.getStartTime());
        assertEquals(bookingDto.get(0).getEnd(), booking.getEndTime());

    }

    @Test
    public void testGetAllBookingsByBookerIdStatusPastWithPagination() {

        Booking booking = builder().id(1L).status(WAITING)
                .startTime(LocalDateTime.now().plusDays(3))
                .endTime(LocalDateTime.now().plusDays(5)).build();

        int from = 0;
        int size = 5;

        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(bookingRepository.findAllByBookerIdAndEndTimeIsBeforeOrderByStartTimeDesc(anyLong(),
                any(LocalDateTime.class), eq(PageRequest.of(from, size))))
                .thenReturn(new PageImpl<>(List.of(booking)));

        when(bookingDtoMapper.toBookingDto(List.of(booking), userDtoMapper, itemDtoMapper))
                .thenReturn(List.of(BookingDto.builder().id(1L).status(booking.getStatus())
                        .start(booking.getStartTime())
                        .end(booking.getEndTime()).build()));
        BookingGetRequest request = BookingGetRequest.builder()
                .userId(1L)
                .possibleState(String.valueOf(PAST))
                .from(from)
                .size(size).build();

        List<BookingDto> bookingDto = bookingService.getAllByBookerId(request);

        assertEquals(bookingDto.get(0).getId(), 1L);
        assertEquals(bookingDto.get(0).getStatus(), booking.getStatus());
        assertEquals(bookingDto.get(0).getId(), booking.getId());
        assertEquals(bookingDto.get(0).getStart(), booking.getStartTime());
        assertEquals(bookingDto.get(0).getEnd(), booking.getEndTime());

    }

    @Test
    public void testGetAllBookingsStatusNotAllowed() {

        BookingGetRequest request = BookingGetRequest.builder()
                .userId(1L)
                .possibleState("INVALID")
                .from(null)
                .size(null).build();

        when(userRepository.existsById(anyLong())).thenReturn(true);

        assertThrows(IncorrectStateException.class, () -> bookingService.getAllByBookerItems(request));

    }


    @Test
    public void testGetAllBookingsByBookerIdStatusPastForOwner() {

        Booking booking = builder().id(1L).status(WAITING)
                .startTime(now().minusDays(5))
                .endTime(now().minusDays(3))
                .build();

        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(bookingRepository.findBookingsByItemOwnerIdAndEndTimeIsBeforeOrderByStartTimeDesc(anyLong(),
                any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        when(bookingDtoMapper.toBookingDto(List.of(booking), userDtoMapper, itemDtoMapper))
                .thenReturn(List.of(BookingDto.builder()
                        .id(1L)
                        .start(booking.getStartTime())
                        .end(booking.getEndTime())
                        .status(booking.getStatus()).build()));


        BookingGetRequest request = BookingGetRequest.builder()
                .userId(1L)
                .possibleState(String.valueOf(PAST))
                .from(null)
                .size(null).build();

        List<BookingDto> bookingDto = bookingService.getAllByBookerItems(request);
        assertEquals(bookingDto.get(0).getId(), booking.getId());
        assertEquals(bookingDto.get(0).getStatus(), booking.getStatus());
        assertEquals(bookingDto.get(0).getStart(), booking.getStartTime());
        assertEquals(bookingDto.get(0).getEnd(), booking.getEndTime());

    }

    @Test
    public void testGetAllBookingsByBookerIdStatusCurrentForOwner() {

        Booking booking = builder().id(1L).status(WAITING)
                .startTime(now().minusDays(5))
                .endTime(now().plusDays(3))
                .build();

        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(bookingRepository.findBookingsByItemOwnerIdAndStartTimeIsBeforeAndEndTimeIsAfter(anyLong(),
                any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        when(bookingDtoMapper.toBookingDto(List.of(booking), userDtoMapper, itemDtoMapper))
                .thenReturn(List.of(BookingDto.builder()
                        .id(1L)
                        .start(booking.getStartTime())
                        .end(booking.getEndTime())
                        .status(booking.getStatus()).build()));


        BookingGetRequest request = BookingGetRequest.builder()
                .userId(1L)
                .possibleState(String.valueOf(CURRENT))
                .from(null)
                .size(null).build();

        List<BookingDto> bookingDto = bookingService.getAllByBookerItems(request);
        assertEquals(bookingDto.get(0).getId(), booking.getId());
        assertEquals(bookingDto.get(0).getStatus(), booking.getStatus());
        assertEquals(bookingDto.get(0).getStart(), booking.getStartTime());
        assertEquals(bookingDto.get(0).getEnd(), booking.getEndTime());

    }


    @Test
    public void testGetAllBookingsByBookerIdStatusFutureForOwner() {

        Booking booking = builder().id(1L).status(WAITING)
                .startTime(now().plusDays(3))
                .endTime(now().plusDays(5))
                .build();

        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(bookingRepository.findBookingsByItemOwnerIdAndStartTimeIsAfterOrderByStartTimeDesc(anyLong(),
                any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        when(bookingDtoMapper.toBookingDto(List.of(booking), userDtoMapper, itemDtoMapper))
                .thenReturn(List.of(BookingDto.builder()
                        .id(1L)
                        .start(booking.getStartTime())
                        .end(booking.getEndTime())
                        .status(booking.getStatus()).build()));


        BookingGetRequest request = BookingGetRequest.builder()
                .userId(1L)
                .possibleState(String.valueOf(FUTURE))
                .from(null)
                .size(null).build();

        List<BookingDto> bookingDto = bookingService.getAllByBookerItems(request);
        System.out.println(bookingDto);
        assertEquals(bookingDto.get(0).getId(), booking.getId());
        assertEquals(bookingDto.get(0).getStatus(), booking.getStatus());
        assertEquals(bookingDto.get(0).getStart(), booking.getStartTime());
        assertEquals(bookingDto.get(0).getEnd(), booking.getEndTime());

    }


    @Test
    public void testGetAllBookingsByBookerIdStatusPastWithPaginationForOwner() {

        Booking booking = builder().id(1L).status(WAITING)
                .startTime(now().minusDays(5))
                .endTime(now().minusDays(3))
                .build();

        int from = 0;
        int size = 5;

        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(bookingRepository.findBookingsByItemOwnerIdAndEndTimeIsBeforeOrderByStartTimeDesc(anyLong(),
                any(LocalDateTime.class), eq(PageRequest.of(from, size))))
                .thenReturn(new PageImpl<>(List.of(booking)));

        when(bookingDtoMapper.toBookingDto(List.of(booking), userDtoMapper, itemDtoMapper))
                .thenReturn(List.of(BookingDto.builder()
                        .id(1L)
                        .start(booking.getStartTime())
                        .end(booking.getEndTime())
                        .status(booking.getStatus()).build()));

        BookingGetRequest request = BookingGetRequest.builder()
                .userId(1L)
                .possibleState(String.valueOf(PAST))
                .from(from)
                .size(size).build();

        List<BookingDto> bookingDto = bookingService.getAllByBookerItems(request);

        assertEquals(bookingDto.get(0).getId(), 1L);
        assertEquals(bookingDto.get(0).getStatus(), booking.getStatus());
        assertEquals(bookingDto.get(0).getStart(), booking.getStartTime());
        assertEquals(bookingDto.get(0).getEnd(), booking.getEndTime());

    }

    @Test
    public void testGetAllBookingsByBookerIdStatusCurrentWithPaginationForOwner() {

        Booking booking = builder().id(1L).status(WAITING)
                .startTime(now().minusDays(5))
                .endTime(now().minusDays(3))
                .build();

        int from = 0;
        int size = 5;

        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(bookingRepository.findBookingsByItemOwnerIdAndStartTimeIsBeforeAndEndTimeIsAfter(anyLong(),
                any(LocalDateTime.class), any(LocalDateTime.class), eq(PageRequest.of(from, size))))
                .thenReturn(new PageImpl<>(List.of(booking)));

        when(bookingDtoMapper.toBookingDto(List.of(booking), userDtoMapper, itemDtoMapper))
                .thenReturn(List.of(BookingDto.builder()
                        .id(1L)
                        .start(booking.getStartTime())
                        .end(booking.getEndTime())
                        .status(booking.getStatus()).build()));

        BookingGetRequest request = BookingGetRequest.builder()
                .userId(1L)
                .possibleState(String.valueOf(CURRENT))
                .from(from)
                .size(size).build();

        List<BookingDto> bookingDto = bookingService.getAllByBookerItems(request);

        assertEquals(bookingDto.get(0).getId(), 1L);
        assertEquals(bookingDto.get(0).getStatus(), booking.getStatus());
        assertEquals(bookingDto.get(0).getStart(), booking.getStartTime());
        assertEquals(bookingDto.get(0).getEnd(), booking.getEndTime());

    }

    @Test
    public void testGetAllBookingsByBookerIdStatusFutureWithPaginationForOwner() {

        Booking booking = builder().id(1L).status(WAITING)
                .startTime(now().plusDays(3))
                .endTime(now().plusDays(5))
                .build();

        int from = 0;
        int size = 5;

        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(bookingRepository.findBookingsByItemOwnerIdAndStartTimeIsAfterOrderByStartTimeDesc(anyLong(),
                any(LocalDateTime.class), eq(PageRequest.of(from, size))))
                .thenReturn(new PageImpl<>(List.of(booking)));

        when(bookingDtoMapper.toBookingDto(List.of(booking), userDtoMapper, itemDtoMapper))
                .thenReturn(List.of(BookingDto.builder()
                        .id(1L)
                        .start(booking.getStartTime())
                        .end(booking.getEndTime())
                        .status(booking.getStatus()).build()));

        BookingGetRequest request = BookingGetRequest.builder()
                .userId(1L)
                .possibleState(String.valueOf(FUTURE))
                .from(from)
                .size(size).build();

        List<BookingDto> bookingDto = bookingService.getAllByBookerItems(request);

        assertEquals(bookingDto.get(0).getId(), 1L);
        assertEquals(bookingDto.get(0).getStatus(), booking.getStatus());
        assertEquals(bookingDto.get(0).getStart(), booking.getStartTime());
        assertEquals(bookingDto.get(0).getEnd(), booking.getEndTime());

    }
}




