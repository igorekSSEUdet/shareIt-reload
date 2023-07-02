package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingCreationRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.mapper.BookingDtoMapper;
import ru.practicum.shareit.booking.repository.model.Booking;
import ru.practicum.shareit.item.dto.ShortItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.ShortUserDto;
import ru.practicum.shareit.user.mapper.ItemDtoMapper;
import ru.practicum.shareit.user.mapper.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class BookingDtoMapperTest {

    @Mock
    private UserDtoMapper userDtoMapper;
    @Mock
    private ItemDtoMapper itemDtoMapper;
    @InjectMocks
    private BookingDtoMapper mapper;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    @Test
    public void toBookingTest() {
        BookingCreationRequestDto requestDto = BookingCreationRequestDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(5)).build();

        final long userId = 1L;
        final User user = User.builder().id(1L).name("name").email("mail@mail.ru").build();
        final Item item = Item.builder().id(1L).name("name").description("desc").available(Boolean.FALSE).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(userId)).thenReturn(Optional.of(item));

        Booking result = mapper.toBooking(requestDto, userId);

        assertEquals(result.getStartTime(), requestDto.getStart());
        assertEquals(result.getEndTime(), requestDto.getEnd());
        assertEquals(result.getItem().getId(), requestDto.getItemId());

        assertEquals(result.getItem().getName(), item.getName());
        assertEquals(result.getItem().getDescription(), item.getDescription());
        assertEquals(result.getItem().getAvailable(), item.getAvailable());

        assertEquals(result.getBooker().getId(), userId);
        assertEquals(result.getBooker().getName(), user.getName());
        assertEquals(result.getBooker().getEmail(), user.getEmail());

    }

    @Test
    public void toBookingDtoTest() {

        User owner = User.builder().id(2L).name("name").email("mail@mail.ru").build();
        User booker = User.builder().id(1L).name("booker").email("booker@mail.ru").build();
        Item item = Item.builder().id(1L).name("name").description("desc").available(Boolean.TRUE)
                .owner(owner).build();

        Booking requestBooking = Booking.builder()
                .id(1L)
                .status(Booking.Status.WAITING)
                .startTime(LocalDateTime.now().plusDays(3))
                .endTime(LocalDateTime.now().plusDays(5))
                .item(item)
                .booker(booker).build();

        when(userDtoMapper.toShortUserDto(any())).thenReturn(ShortUserDto.builder().id(booker.getId()).build());

        when(itemDtoMapper.toShortItemDto(any())).thenReturn(ShortItemDto.builder()
                .id(item.getId())
                .name(item.getName()).build());

        BookingDto bookingDto = mapper.toBookingDto(requestBooking, userDtoMapper, itemDtoMapper);

        assertEquals(bookingDto.getId(), requestBooking.getId());
        assertEquals(bookingDto.getStart(), requestBooking.getStartTime());
        assertEquals(bookingDto.getEnd(), requestBooking.getEndTime());
        assertEquals(bookingDto.getStatus(), requestBooking.getStatus());
        assertEquals(bookingDto.getBooker().getId(), requestBooking.getBooker().getId());
        assertEquals(bookingDto.getItem().getId(), requestBooking.getItem().getId());
        assertEquals(bookingDto.getItem().getName(), requestBooking.getItem().getName());

    }

    @Test
    public void toBookingDtoTestWithCollection() {

        User owner = User.builder().id(2L).name("name").email("mail@mail.ru").build();
        User booker = User.builder().id(1L).name("booker").email("booker@mail.ru").build();
        Item item = Item.builder().id(1L).name("name").description("desc").available(Boolean.TRUE)
                .owner(owner).build();

        Booking requestBooking = Booking.builder()
                .id(1L)
                .status(Booking.Status.WAITING)
                .startTime(LocalDateTime.now().plusDays(3))
                .endTime(LocalDateTime.now().plusDays(5))
                .item(item)
                .booker(booker).build();

        when(userDtoMapper.toShortUserDto(any())).thenReturn(ShortUserDto.builder().id(booker.getId()).build());

        when(itemDtoMapper.toShortItemDto(any())).thenReturn(ShortItemDto.builder()
                .id(item.getId())
                .name(item.getName()).build());

        Collection<Booking> bookings = List.of(requestBooking);

        List<BookingDto> result = mapper.toBookingDto(bookings, userDtoMapper, itemDtoMapper);

        assertEquals(result.get(0).getId(), requestBooking.getId());
        assertEquals(result.get(0).getStart(), requestBooking.getStartTime());
        assertEquals(result.get(0).getEnd(), requestBooking.getEndTime());
        assertEquals(result.get(0).getStatus(), requestBooking.getStatus());
        assertEquals(result.get(0).getBooker().getId(), requestBooking.getBooker().getId());
        assertEquals(result.get(0).getItem().getId(), requestBooking.getItem().getId());
        assertEquals(result.get(0).getItem().getName(), requestBooking.getItem().getName());
    }


    @Test
    public void toShortBookingDtoTest() {

        User owner = User.builder().id(2L).name("name").email("mail@mail.ru").build();
        User booker = User.builder().id(1L).name("booker").email("booker@mail.ru").build();
        Item item = Item.builder().id(1L).name("name").description("desc").available(Boolean.TRUE)
                .owner(owner).build();

        Booking requestBooking = Booking.builder()
                .id(1L)
                .status(Booking.Status.WAITING)
                .startTime(LocalDateTime.now().plusDays(3))
                .endTime(LocalDateTime.now().plusDays(5))
                .item(item)
                .booker(booker).build();
        ShortBookingDto dto = ShortBookingDto.builder().id(requestBooking.getId()).bookerId(requestBooking.getBooker().getId()).build();
        ShortBookingDto result = mapper.toShortBookingDto(requestBooking);
        assertEquals(result.getBookerId(), dto.getBookerId());
        assertEquals(result.getId(), requestBooking.getId());

    }

}
