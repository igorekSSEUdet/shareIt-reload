package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreationRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.IncorrectStateException;
import ru.practicum.shareit.booking.mapper.BookingDtoMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.repository.model.Booking;
import ru.practicum.shareit.booking.repository.model.Booking.Status;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.ItemDtoMapper;
import ru.practicum.shareit.user.mapper.UserDtoMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.LocalDateTime.now;
import static ru.practicum.shareit.booking.service.BookingService.*;
import static ru.practicum.shareit.item.service.ItemService.checkItemExistsById;
import static ru.practicum.shareit.item.service.ItemService.checkOwnerOfItemByItemIdAndUserId;
import static ru.practicum.shareit.user.service.UserService.checkUserExistsById;

@Service
@Slf4j
@Transactional
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingDtoMapper bookingDtoMapper;
    private final UserDtoMapper userDtoMapper;
    private final ItemDtoMapper itemDtoMapper;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              UserRepository userRepository,
                              ItemRepository itemRepository,
                              BookingDtoMapper bookingDtoMapper,
                              UserDtoMapper userDtoMapper,
                              ItemDtoMapper itemDtoMapper) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingDtoMapper = bookingDtoMapper;
        this.userDtoMapper = userDtoMapper;
        this.itemDtoMapper = itemDtoMapper;
    }

    @Override
    public BookingDto addBooking(BookingCreationRequestDto bookingDto, Long userId) {
        checkUserExistsById(userRepository, userId);
        checkItemExistsById(itemRepository, bookingDto.getItemId());
        checkUserNotOwnerByItemIdAndUserId(itemRepository, bookingDto.getItemId(), userId);
        checkBookingTimePeriod(bookingDto.getStart(), bookingDto.getEnd());

        Booking booking = bookingDtoMapper.toBooking(bookingDto, userId);
        checkItemAvailableForBooking(booking.getItem());

        Booking savedBooking = bookingRepository.save(booking);
        log.debug("Booking ID_{} added.", savedBooking.getId());

        return bookingDtoMapper.toBookingDto(savedBooking, userDtoMapper, itemDtoMapper);
    }

    @Override
    public BookingDto updateBookingStatus(Long bookingId, Boolean approved, Long userId) {
        checkBookingExistsById(bookingRepository, bookingId);
        checkUserExistsById(userRepository, userId);

        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        checkOwnerOfItemByItemIdAndUserId(itemRepository, booking.getItem().getId(), userId);
        checkBookingStatusNotApprove(booking);

        booking.setStatus((approved == Boolean.TRUE) ? (Status.APPROVED) : (Status.REJECTED));

        log.debug("Booking ID_{} updated.", bookingId);
        Booking updatedBooking = bookingRepository.save(booking);

        return bookingDtoMapper.toBookingDto(updatedBooking, userDtoMapper, itemDtoMapper);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "booking", key = "#bookingId")
    public BookingDto getBooking(Long bookingId, Long userId) {
        checkBookingExistsById(bookingRepository, bookingId);
        checkUserExistsById(userRepository, userId);

        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        checkOwnerOrBooker(booking, userId);

        log.debug("Booking ID_{} returned.", booking.getId());
        return bookingDtoMapper.toBookingDto(booking, userDtoMapper, itemDtoMapper);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllByBookerId(@Valid BookingGetRequest request) {
        checkUserExistsById(userRepository, request.getUserId());
        boolean isPagination = checkIsPagination(request.getFrom(), request.getSize());

        State state = checkState(request.getPossibleState());
        switch (state) {
            case ALL:
                return bookingDtoMapper.toBookingDto(getBookingsByAllState(isPagination, request),
                        userDtoMapper, itemDtoMapper);
            case CURRENT:
                return bookingDtoMapper.toBookingDto(getBookingsByCurrentState(isPagination, request),
                        userDtoMapper, itemDtoMapper);
            case PAST:
                return bookingDtoMapper.toBookingDto(getBookingsByPastState(isPagination, request),
                        userDtoMapper, itemDtoMapper);
            case FUTURE:
                return bookingDtoMapper.toBookingDto(getBookingsByFutureState(isPagination, request),
                        userDtoMapper, itemDtoMapper);
            case WAITING:
                return bookingDtoMapper.toBookingDto(getBookingsByWaitingState(isPagination, request),
                        userDtoMapper, itemDtoMapper);
            case REJECTED:
                return bookingDtoMapper.toBookingDto(getBookingsByRejectedState(isPagination, request),
                        userDtoMapper, itemDtoMapper);
            default:
                throw IncorrectStateException.getFromIncorrectState(request.getPossibleState());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllByBookerItems(BookingGetRequest request) {
        checkUserExistsById(userRepository, request.getUserId());
        boolean isPagination = checkIsPagination(request.getFrom(), request.getSize());

        State state = checkState(request.getPossibleState());
        switch (state) {
            case ALL:
                return bookingDtoMapper.toBookingDto(getBookingsByOwnerWithAllState(isPagination, request),
                        userDtoMapper, itemDtoMapper);
            case CURRENT:
                return bookingDtoMapper.toBookingDto(getBookingsByOwnerWithCurrentState(isPagination, request),
                        userDtoMapper, itemDtoMapper);
            case PAST:
                return bookingDtoMapper.toBookingDto(getBookingsByOwnerWithPastState(isPagination, request),
                        userDtoMapper, itemDtoMapper);
            case FUTURE:
                return bookingDtoMapper.toBookingDto(getBookingsByOwnerWithFutureState(isPagination, request),
                        userDtoMapper, itemDtoMapper);
            case WAITING:
                return bookingDtoMapper.toBookingDto(getBookingsByOwnerWithWaitingState(isPagination, request),
                        userDtoMapper, itemDtoMapper);
            case REJECTED:
                return bookingDtoMapper.toBookingDto(getBookingsByOwnerWithRejectedState(isPagination, request),
                        userDtoMapper, itemDtoMapper);
            default:
                throw IncorrectStateException.getFromIncorrectState(request.getPossibleState());
        }
    }

    private List<Booking> getBookingsByAllState(boolean isPagination, BookingGetRequest request) {
        return (isPagination) ?
                getAllBookingsByBookerIdWithPagination(request.getUserId(),
                        prepareFrom(request.getFrom()), request.getSize()) :
                getAllBookingsByBookerId(request.getUserId());
    }

    private List<Booking> getBookingsByCurrentState(boolean isPagination, BookingGetRequest request) {
        return (isPagination) ? getCurrentBookingsByBookerIdWithPagination(request.getUserId(),
                prepareFrom(request.getFrom()), request.getSize()) :
                getCurrentBookingsByBookerId(request.getUserId());
    }

    private List<Booking> getBookingsByPastState(boolean isPagination, BookingGetRequest request) {
        return (isPagination) ? getPastBookingsByBookerIdWithPagination(request.getUserId(),
                prepareFrom(request.getFrom()), request.getSize()) :
                getPastBookingsByBookerId(request.getUserId());
    }

    private List<Booking> getBookingsByFutureState(boolean isPagination, BookingGetRequest request) {
        return (isPagination) ? getFutureBookingsByBookerIdWithPagination(request.getUserId(),
                prepareFrom(request.getFrom()), request.getSize()) :
                getFutureBookingsByBookerId(request.getUserId());
    }

    private List<Booking> getBookingsByWaitingState(boolean isPagination, BookingGetRequest request) {
        return (isPagination) ? getWaitingBookingsByBookerIdWithPagination(request.getUserId(),
                prepareFrom(request.getFrom()), request.getSize()) :
                getWaitingBookingsByBookerId(request.getUserId());
    }

    private List<Booking> getBookingsByRejectedState(boolean isPagination, BookingGetRequest request) {
        return (isPagination) ? getRejectedBookingsByBookerIdWithPagination(request.getUserId(),
                prepareFrom(request.getFrom()), request.getSize()) :
                getRejectedBookingsByBookerId(request.getUserId());
    }

    private List<Booking> getBookingsByOwnerWithAllState(boolean isPagination, BookingGetRequest request) {
        return (isPagination) ? getAllBookingsByOwnerIdWithPagination(request.getUserId(),
                prepareFrom(request.getFrom()), request.getSize()) :
                getAllBookingsByOwnerId(request.getUserId());
    }

    private List<Booking> getBookingsByOwnerWithCurrentState(boolean isPagination, BookingGetRequest request) {
        return (isPagination) ? getCurrentBookingsByOwnerIdWithPagination(request.getUserId(),
                prepareFrom(request.getFrom()), request.getSize()) :
                getCurrentBookingsByOwnerId(request.getUserId());
    }

    private List<Booking> getBookingsByOwnerWithPastState(boolean isPagination, BookingGetRequest request) {
        return (isPagination) ? getPastBookingsByOwnerIdWithPagination(request.getUserId(),
                prepareFrom(request.getFrom()), request.getSize()) :
                getPastBookingsByOwnerId(request.getUserId());
    }

    private List<Booking> getBookingsByOwnerWithFutureState(boolean isPagination, BookingGetRequest request) {
        return (isPagination) ? getFutureBookingsByOwnerIdWithPagination(request.getUserId(),
                prepareFrom(request.getFrom()), request.getSize()) :
                getFutureBookingsByOwnerId(request.getUserId());
    }

    private List<Booking> getBookingsByOwnerWithWaitingState(boolean isPagination, BookingGetRequest request) {
        return (isPagination) ? getWaitingBookingsByOwnerIdWithPagination(request.getUserId(),
                prepareFrom(request.getFrom()), request.getSize()) :
                getWaitingBookingsByOwnerId(request.getUserId());
    }

    private List<Booking> getBookingsByOwnerWithRejectedState(boolean isPagination, BookingGetRequest request) {
        return (isPagination) ? getRejectedBookingsByOwnerIdWithPagination(request.getUserId(),
                prepareFrom(request.getFrom()), request.getSize()) :
                getRejectedBookingsByOwnerId(request.getUserId());
    }


    private List<Booking> getAllBookingsByBookerId(Long bookerId) {
        return bookingRepository.findBookingByBookerIdOrderByStartTimeDesc(bookerId);
    }

    private List<Booking> getCurrentBookingsByBookerId(Long bookerId) {
        LocalDateTime time = now();
        return bookingRepository.findBookingByBookerIdAndStartTimeIsBeforeAndEndTimeIsAfter(bookerId, time, time);
    }

    private List<Booking> getPastBookingsByBookerId(Long bookerId) {
        return bookingRepository.findAllByBookerIdAndEndTimeIsBeforeOrderByStartTimeDesc(bookerId, now());
    }

    private List<Booking> getFutureBookingsByBookerId(Long bookerId) {
        return bookingRepository.findBookingsByBookerIdAndStartTimeIsAfterOrderByStartTimeDesc(bookerId, now());
    }

    private List<Booking> getWaitingBookingsByBookerId(Long bookerId) {
        return bookingRepository.findBookingsByBookerIdAndStatusEquals(bookerId, Status.WAITING);
    }

    private List<Booking> getRejectedBookingsByBookerId(Long bookerId) {
        return bookingRepository.findBookingsByBookerIdAndStatusEquals(bookerId, Status.REJECTED);
    }

    private List<Booking> getAllBookingsByOwnerId(Long ownerId) {
        return bookingRepository.findBookingsByItemOwnerIdOrderByStartTimeDesc(ownerId);
    }

    private List<Booking> getCurrentBookingsByOwnerId(Long ownerId) {
        LocalDateTime time = LocalDateTime.now();
        return bookingRepository.findBookingsByItemOwnerIdAndStartTimeIsBeforeAndEndTimeIsAfter(ownerId, time, time);
    }

    private List<Booking> getPastBookingsByOwnerId(Long ownerId) {

        return bookingRepository.findBookingsByItemOwnerIdAndEndTimeIsBeforeOrderByStartTimeDesc(ownerId, now());
    }

    private List<Booking> getFutureBookingsByOwnerId(Long ownerId) {
        return bookingRepository.findBookingsByItemOwnerIdAndStartTimeIsAfterOrderByStartTimeDesc(ownerId, now());
    }

    private List<Booking> getWaitingBookingsByOwnerId(Long ownerId) {
        return bookingRepository.findBookingsByItemOwnerIdAndStatusEquals(ownerId, Status.WAITING);
    }

    private List<Booking> getRejectedBookingsByOwnerId(Long ownerId) {
        return bookingRepository.findBookingsByItemOwnerIdAndStatusEquals(ownerId, Status.REJECTED);
    }

    private List<Booking> getAllBookingsByOwnerIdWithPagination(Long ownerId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        return bookingRepository.findBookingsByItemOwnerIdOrderByStartTimeAsc(ownerId, pageable).getContent();
    }

    private Integer prepareFrom(Integer from) {
        if (from == 0) {
            return from;
        }
        if (from <= 1) {
            return from - 1;
        }
        return from - 2;
    }

    private List<Booking> getCurrentBookingsByOwnerIdWithPagination(Long ownerId, Integer from, Integer size) {
        LocalDateTime time = LocalDateTime.now();
        Pageable pageable = PageRequest.of(from, size);
        return bookingRepository.findBookingsByItemOwnerIdAndStartTimeIsBeforeAndEndTimeIsAfter(ownerId, time, time, pageable).getContent();
    }

    private List<Booking> getPastBookingsByOwnerIdWithPagination(Long ownerId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        return bookingRepository.findBookingsByItemOwnerIdAndEndTimeIsBeforeOrderByStartTimeDesc(ownerId, now(), pageable).getContent();
    }

    private List<Booking> getFutureBookingsByOwnerIdWithPagination(Long ownerId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        return bookingRepository.findBookingsByItemOwnerIdAndStartTimeIsAfterOrderByStartTimeDesc(ownerId, now(), pageable).getContent();
    }

    private List<Booking> getWaitingBookingsByOwnerIdWithPagination(Long ownerId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        return bookingRepository.findBookingsByItemOwnerIdAndStatusEquals(ownerId, Status.WAITING, pageable)
                .getContent();
    }

    private List<Booking> getRejectedBookingsByOwnerIdWithPagination(Long ownerId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        return bookingRepository.findBookingsByItemOwnerIdAndStatusEquals(ownerId, Status.REJECTED, pageable)
                .getContent();
    }

    private List<Booking> getAllBookingsByBookerIdWithPagination(Long bookerId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);

        return bookingRepository.findBookingByBookerIdOrderByStartTimeDesc(bookerId, pageable).getContent();
    }

    private List<Booking> getCurrentBookingsByBookerIdWithPagination(Long bookerId, Integer from, Integer size) {
        LocalDateTime time = now();
        Pageable pageable = PageRequest.of(from, size);

        return bookingRepository.findBookingByBookerIdAndStartTimeIsBeforeAndEndTimeIsAfter(bookerId, time, time, pageable).getContent();
    }

    private List<Booking> getPastBookingsByBookerIdWithPagination(Long bookerId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);

        return bookingRepository.findAllByBookerIdAndEndTimeIsBeforeOrderByStartTimeDesc(bookerId, now(), pageable)
                .getContent();
    }

    private List<Booking> getFutureBookingsByBookerIdWithPagination(Long bookerId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);

        return bookingRepository.findBookingsByBookerIdAndStartTimeIsAfterOrderByStartTimeDesc(bookerId, now(), pageable)
                .getContent();
    }

    private List<Booking> getWaitingBookingsByBookerIdWithPagination(Long bookerId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);

        return bookingRepository.findBookingsByBookerIdAndStatusEquals(bookerId, Status.WAITING, pageable).getContent();
    }

    private List<Booking> getRejectedBookingsByBookerIdWithPagination(Long bookerId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);

        return bookingRepository.findBookingsByBookerIdAndStatusEquals(bookerId, Status.REJECTED, pageable)
                .getContent();
    }

    private boolean checkIsPagination(Integer from, Integer size) {
        return from != null && size != null;
    }

}

