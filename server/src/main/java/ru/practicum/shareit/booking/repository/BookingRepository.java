package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.repository.model.Booking;
import ru.practicum.shareit.booking.repository.model.Booking.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findBookingByBookerIdOrderByStartTimeDesc(
            Long bookerId);

    List<Booking> findBookingByBookerIdAndStartTimeIsBeforeAndEndTimeIsAfter(
            Long bookerId, LocalDateTime time1, LocalDateTime time2);

    List<Booking> findAllByBookerIdAndEndTimeIsBeforeOrderByStartTimeDesc(
            Long bookerId, LocalDateTime time);

    List<Booking> findBookingsByBookerIdAndStartTimeIsAfterOrderByStartTimeDesc(
            Long bookerId, LocalDateTime time);

    List<Booking> findBookingsByBookerIdAndStatusEquals(
            Long bookerId, Status status);

    List<Booking> findBookingsByItemOwnerIdOrderByStartTimeDesc(
            Long ownerId);

    List<Booking> findBookingsByItemOwnerIdAndStartTimeIsBeforeAndEndTimeIsAfter(
            Long ownerId, LocalDateTime time1, LocalDateTime time2);

    List<Booking> findBookingsByItemOwnerIdAndEndTimeIsBeforeOrderByStartTimeDesc(
            Long ownerId, LocalDateTime time);

    List<Booking> findBookingsByItemOwnerIdAndStartTimeIsAfterOrderByStartTimeDesc(
            Long ownerId, LocalDateTime time);

    List<Booking> findBookingsByItemOwnerIdAndStatusEquals(
            Long ownerId, Status status);

    boolean existsByBookerIdAndItemIdAndEndTimeIsBefore(
            Long bookerId, Long itemId, LocalDateTime time);

    List<Booking> findAllByItemId(Long itemId);

    List<Booking> findAllByItemIdAndStatus(Long itemId, Status status);

    List<Booking> findAllByItemOwnerId(Long ownerId);

    List<Booking> findAllByItem_OwnerIdAndStatus(Long ownerId, Status status);

    Page<Booking> findBookingByBookerIdOrderByStartTimeDesc(
            Long bookerId, Pageable pageable);

    Page<Booking> findBookingByBookerIdAndStartTimeIsBeforeAndEndTimeIsAfter(
            Long bookerId, LocalDateTime time1, LocalDateTime time2, Pageable pageable);

    Page<Booking> findAllByBookerIdAndEndTimeIsBeforeOrderByStartTimeDesc(
            Long bookerId, LocalDateTime time, Pageable pageable);

    Page<Booking> findBookingsByBookerIdAndStartTimeIsAfterOrderByStartTimeDesc(
            Long bookerId, LocalDateTime time, Pageable pageable);

    Page<Booking> findBookingsByBookerIdAndStatusEquals(
            Long bookerId, Status status, Pageable pageable);

    Page<Booking> findBookingsByItemOwnerIdOrderByStartTimeAsc(
            Long ownerId, Pageable pageable);

    Page<Booking> findBookingsByItemOwnerIdAndStartTimeIsBeforeAndEndTimeIsAfter(
            Long ownerId, LocalDateTime time1, LocalDateTime time2, Pageable pageable);

    Page<Booking> findBookingsByItemOwnerIdAndEndTimeIsBeforeOrderByStartTimeDesc(
            Long ownerId, LocalDateTime time, Pageable pageable);

    Page<Booking> findBookingsByItemOwnerIdAndStartTimeIsAfterOrderByStartTimeDesc(
            Long ownerId, LocalDateTime time, Pageable pageable);

    Page<Booking> findBookingsByItemOwnerIdAndStatusEquals(
            Long ownerId, Status status, Pageable pageable);


}
