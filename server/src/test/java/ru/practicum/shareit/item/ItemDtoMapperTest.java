package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.mapper.BookingDtoMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.repository.model.Booking;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.comment.Comment;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.repository.comment.CommentRepository;
import ru.practicum.shareit.request.exceptions.RequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.RequestRepository;
import ru.practicum.shareit.user.mapper.CommentDtoMapper;
import ru.practicum.shareit.user.mapper.ItemDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.repository.model.Booking.Status.APPROVED;

@ExtendWith(MockitoExtension.class)
public class ItemDtoMapperTest {

    @InjectMocks
    private ItemDtoMapper mapper;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;

    @Test
    public void toItemTest() {
        final ItemCreationRequestDto requestDto = ItemCreationRequestDto.builder()
                .name("name")
                .description("desc")
                .available(Boolean.TRUE).build();
        final long ownerId = 1L;

        User user = User.builder().build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Item item = mapper.toItem(requestDto, ownerId);
        assertEquals(item.getName(), requestDto.getName());
        assertEquals(item.getDescription(), requestDto.getDescription());
        assertEquals(item.getAvailable(), requestDto.getAvailable());


    }

    @Test
    public void toItemTestRequestNotFoundException() {
        final ItemCreationRequestDto requestDto = ItemCreationRequestDto.builder()
                .name("name")
                .description("desc")
                .requestId(2L)
                .available(Boolean.TRUE).build();
        final long ownerId = 1L;

        User user = User.builder().build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        assertThrows(RequestNotFoundException.class, () -> mapper.toItem(requestDto, ownerId));

    }

    @Test
    public void toItemWithItemIdTest() {
        final ItemUpdateRequestDto requestDto = ItemUpdateRequestDto.builder()
                .name("name")
                .description("desc")
                .available(Boolean.TRUE).build();
        final long ownerId = 1L;
        final long itemId = 1L;
        User user = User.builder().build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        Item item = Item.builder().name("name").description("desc").available(Boolean.FALSE).build();
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        Item result = mapper.toItem(requestDto, itemId, ownerId);
        System.out.println(result);
        assertEquals(result.getName(), requestDto.getName().get());
        assertEquals(result.getDescription(), requestDto.getDescription().get());
        assertEquals(result.getAvailable(), requestDto.getAvailable().get());


    }

    @Test
    public void toItemDtoTest() {
        Item item = Item.builder().name("name").description("desc").available(Boolean.FALSE)
                .request(ItemRequest.builder().id(1L).build()).build();
        ItemDto result = mapper.toItemDto(item);
        assertEquals(result.getName(), item.getName());
        assertEquals(result.getDescription(), item.getDescription());
        assertEquals(result.getAvailable(), item.getAvailable());
        assertEquals(result.getRequestId(), item.getRequest().getId());


    }

    @Test
    public void toShortItemDto() {
        Item item = Item.builder().name("name").description("desc").available(Boolean.FALSE)
                .request(ItemRequest.builder().id(1L).build()).build();
        ShortItemDto result = mapper.toShortItemDto(item);
        assertEquals(result.getName(), item.getName());
        assertEquals(result.getId(), item.getId());

    }

    @Test
    public void toDetailedItemDtoTest() {
        Item item = Item.builder().name("name").description("desc").available(Boolean.FALSE)
                .request(ItemRequest.builder().id(1L).build()).build();

        Comment comment = Comment.builder().text("comment").id(1L).created(now())
                .author(User.builder().name("name").build()).build();

        when(commentRepository.findCommentsByItemId(item.getId())).thenReturn(Collections.singletonList(comment));

        CommentDtoMapper commentDtoMapper = mock(CommentDtoMapper.class);

        CommentDto dto = CommentDto.builder().id(comment.getId())
                .text(comment.getText()).created(comment.getCreated())
                .authorName(comment.getAuthor().getName()).build();

        when(commentDtoMapper.toCommentDto(anyList())).thenReturn(Collections.singletonList(dto));

        DetailedItemDto result = mapper.toDetailedItemDto(item, commentDtoMapper);

    }


    @Test
    public void toDetailedItemDtoTestWithBookingDtoMapper() {
        Item item = Item.builder().name("name").description("desc").available(Boolean.FALSE)
                .request(ItemRequest.builder().id(1L).build()).build();

        CommentDtoMapper commentDtoMapper = mock(CommentDtoMapper.class);

        CommentDto dto = CommentDto.builder().id(1L)
                .text("comment").created(now())
                .authorName("name").build();
        when(commentDtoMapper.toCommentDto(anyList())).thenReturn(Collections.singletonList(dto));

        Booking booking = Booking.builder().startTime(now().plusDays(3)).endTime(now().plusDays(4)).id(1L).booker(User.builder().id(1L).build()).build();
        when(bookingRepository.findAllByItemId(item.getId())).thenReturn(Collections.singletonList(booking));

        when(bookingRepository.findAllByItemIdAndStatus(item.getId(), APPROVED))
                .thenReturn(Collections.emptyList());

        BookingDtoMapper bookingDtoMapper = mock(BookingDtoMapper.class);

        DetailedItemDto result = mapper.toDetailedItemDto(item, commentDtoMapper, bookingDtoMapper);
        assertEquals(result.getName(), item.getName());
        assertEquals(result.getDescription(), item.getDescription());
        assertEquals(result.getAvailable(), item.getAvailable());


    }

    @Test
    public void toDetailedItemDtoForOwnerTest() {
        Item item = Item.builder().name("name").description("desc").available(Boolean.FALSE)
                .request(ItemRequest.builder().id(1L).build())
                .owner(User.builder().id(1L).build()).build();

        CommentDtoMapper commentDtoMapper = mock(CommentDtoMapper.class);

        CommentDto dto = CommentDto.builder().id(1L)
                .text("comment").created(now())
                .authorName("name").build();
        when(commentDtoMapper.toCommentDto(anyList())).thenReturn(Collections.singletonList(dto));

        Booking booking = Booking.builder().startTime(now().plusDays(3)).endTime(now().plusDays(4)).id(1L).booker(User.builder().id(1L).build()).build();
        when(bookingRepository.findAllByItemOwnerId(anyLong())).thenReturn(Collections.singletonList(booking));

        when(bookingRepository.findAllByItem_OwnerIdAndStatus(anyLong(), eq(APPROVED)))
                .thenReturn(Collections.emptyList());

        BookingDtoMapper bookingDtoMapper = mock(BookingDtoMapper.class);

        DetailedItemDto result = mapper.toDetailedItemDtoForOwner(item, commentDtoMapper, bookingDtoMapper);

        assertEquals(result.getName(), item.getName());
        assertEquals(result.getDescription(), item.getDescription());
        assertEquals(result.getAvailable(), item.getAvailable());

    }


}
