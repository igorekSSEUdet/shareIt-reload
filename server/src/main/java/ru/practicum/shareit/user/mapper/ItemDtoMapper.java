package ru.practicum.shareit.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Component
public class ItemDtoMapper {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public ItemDtoMapper(ItemRepository itemRepository, UserRepository userRepository, RequestRepository repository, BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.requestRepository = repository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    public Item toItem(ItemCreationRequestDto itemDto, Long ownerId) {
        Item item = new Item();

        User owner = userRepository.findById(ownerId).orElseThrow();

        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(owner);

        if (itemDto.getRequestId() != null) {
            ItemRequest request = requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new RequestNotFoundException("Request not found error"));
            item.setRequest(request);
        }

        return item;
    }


    public Item toItem(ItemUpdateRequestDto itemDto, Long itemId, Long ownerId) {
        Item item = itemRepository.findById(itemId).orElseThrow();

        User owner = userRepository.findById(ownerId).orElseThrow();

        itemDto.getName().ifPresent(item::setName);
        itemDto.getDescription().ifPresent(item::setDescription);
        itemDto.getAvailable().ifPresent(item::setAvailable);
        item.setOwner(owner);

        return item;
    }

    public ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();

        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());

        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        }

        return itemDto;
    }

    public ShortItemDto toShortItemDto(Item item) {
        ShortItemDto itemDto = new ShortItemDto();

        itemDto.setId(item.getId());
        itemDto.setName(item.getName());

        return itemDto;
    }

    public DetailedItemDto toDetailedItemDto(Item item, CommentDtoMapper commentDtoMapper) {
        DetailedItemDto itemDto = new DetailedItemDto();

        List<Comment> comments = commentRepository.findCommentsByItemId(item.getId());

        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setComments(commentDtoMapper.toCommentDto(comments));

        return itemDto;
    }

    public DetailedItemDto toDetailedItemDto(Item item,
                                             CommentDtoMapper commentDtoMapper,
                                             BookingDtoMapper bookingDtoMapper) {

        DetailedItemDto itemDto = toDetailedItemDto(item, commentDtoMapper);

        LocalDateTime time = LocalDateTime.now();
        itemDto.setLastBooking(getLastBooking(bookingDtoMapper, item.getId(), time));
        itemDto.setNextBooking(getNextBooking(bookingDtoMapper, item.getId(), time));

        return itemDto;
    }

    public DetailedItemDto toDetailedItemDtoForOwner(Item item,
                                                     CommentDtoMapper commentDtoMapper,
                                                     BookingDtoMapper bookingDtoMapper) {

        DetailedItemDto itemDto = toDetailedItemDto(item, commentDtoMapper);

        LocalDateTime time = LocalDateTime.now();
        itemDto.setLastBooking(getLastBookingForOwner(bookingDtoMapper, item.getOwner().getId(), time));
        itemDto.setNextBooking(getNextBookingForOwner(bookingDtoMapper, item.getOwner().getId(), time));

        return itemDto;
    }

    public List<ItemDto> toItemDto(Collection<Item> items) {
        return items.stream()
                .map(this::toItemDto)
                .collect(Collectors.toList());
    }

    public List<DetailedItemDto> toDetailedItemDto(Collection<Item> items,
                                                   CommentDtoMapper commentDtoMapper,
                                                   BookingDtoMapper bookingDtoMapper) {
        return items.stream()
                .map(item -> toDetailedItemDto(item, commentDtoMapper, bookingDtoMapper))
                .sorted(Comparator.comparing(DetailedItemDto::getId))
                .collect(Collectors.toList());
    }

    private ShortBookingDto getLastBookingForOwner(BookingDtoMapper bookingDtoMapper,
                                                   Long ownerId, LocalDateTime time) {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerId(ownerId);

        Optional<Booking> lastBooking = bookings.stream()
                .filter(b -> b.getEndTime().isBefore(time))
                .max(Comparator.comparing(Booking::getEndTime));


        if (lastBooking.isEmpty()) {
            Optional<Booking> booking = bookings.stream()
                    .filter(b -> b.getStartTime().isBefore(time))
                    .min(Comparator.comparing(Booking::getEndTime));
            return booking.map(bookingDtoMapper::toShortBookingDto).orElse(null);
        }


        return lastBooking.map(bookingDtoMapper::toShortBookingDto).orElse(null);
    }

    private ShortBookingDto getNextBookingForOwner(BookingDtoMapper bookingDtoMapper,
                                                   Long ownerId, LocalDateTime time) {
        List<Booking> bookings = bookingRepository.findAllByItem_OwnerIdAndStatus(ownerId, Booking.Status.APPROVED);

        Optional<Booking> bookingNext = bookings.stream().sorted(Comparator.comparing(Booking::getStartTime))
                .filter(b -> b.getStartTime().isAfter(time))
                .findFirst();

        return bookingNext.isEmpty() ? null : bookingNext.stream().findFirst()
                .map(bookingDtoMapper::toShortBookingDto).orElse(null);
    }

    private ShortBookingDto getLastBooking(BookingDtoMapper bookingDtoMapper,
                                           Long itemId, LocalDateTime time) {
        List<Booking> bookings = bookingRepository
                .findAllByItemId(itemId);

        Optional<Booking> lastBooking = bookings.stream()
                .filter(b -> b.getEndTime().isBefore(time)).max(Comparator.comparing(Booking::getEndTime));

        return lastBooking.map(bookingDtoMapper::toShortBookingDto).orElse(null);
    }

    private ShortBookingDto getNextBooking(BookingDtoMapper bookingDtoMapper,
                                           Long itemId, LocalDateTime time) {
        List<Booking> bookings = bookingRepository.findAllByItemIdAndStatus(itemId, Booking.Status.APPROVED);

        Optional<Booking> bookingNext = bookings.stream().sorted(Comparator.comparing(Booking::getStartTime))
                .filter(b -> b.getStartTime().isAfter(time)).findFirst();

        return bookingNext.isEmpty() ? null : bookingNext.stream().findFirst()
                .map(bookingDtoMapper::toShortBookingDto).orElse(null);
    }
}
