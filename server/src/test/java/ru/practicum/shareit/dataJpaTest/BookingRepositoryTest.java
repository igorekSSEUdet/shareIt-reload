package ru.practicum.shareit.dataJpaTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.repository.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.booking.repository.model.Booking.Status.WAITING;

@DataJpaTest
public class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private RequestRepository requestRepository;

//    @Test
//    @DirtiesContext
//    public void findBookingByBookerIdOrderByStartTimeDescTest() {
//        User user = User.builder().name("name").email("mail@mail.ru").build();
//        User addedUser = userRepository.save(user);
//
//        Booking booking = Booking.builder()
//                .endTime(now().plusDays(5))
//                .startTime(now().plusDays(3))
//                .status(WAITING)
//                .booker(addedUser).build();
//        bookingRepository.save(booking);
//
//        List<Booking> result = bookingRepository.findBookingByBookerIdOrderByStartTimeDesc(booking.getBooker().getId());
//        assertEquals(result.get(0).getId(), 1L);
//        assertEquals(result.get(0).getBooker().getName(), addedUser.getName());
//        assertEquals(result.get(0).getBooker().getEmail(), addedUser.getEmail());
//        assertEquals(result.get(0).getStatus(), booking.getStatus());
//
//
//        assertEquals(result.get(0).getStartTime(), booking.getStartTime());
//        assertEquals(result.get(0).getEndTime(), booking.getEndTime());
//
//    }

//    @Test
//    @DirtiesContext
//    public void findBookingByBookerIdAndStartTimeIsBeforeAndEndTimeIsAfterTest() {
//        User user = User.builder().name("name").email("mail@mail.ru").build();
//        User addedUser = userRepository.save(user);
//
//        Booking booking = Booking.builder()
//                .startTime(now().minusDays(3))
//                .endTime(now().plusDays(5))
//                .status(WAITING)
//                .booker(addedUser).build();
//        bookingRepository.save(booking);
//
//        List<Booking> result = bookingRepository.findBookingByBookerIdAndStartTimeIsBeforeAndEndTimeIsAfter(
//                booking.getBooker().getId(), now(), now());
//
//        assertEquals(result.get(0).getId(), 1L);
//        assertEquals(result.get(0).getBooker().getName(), addedUser.getName());
//        assertEquals(result.get(0).getBooker().getEmail(), addedUser.getEmail());
//        assertEquals(result.get(0).getStatus(), booking.getStatus());
//
//
//        assertEquals(result.get(0).getStartTime(), booking.getStartTime());
//        assertEquals(result.get(0).getEndTime(), booking.getEndTime());
//
//    }

//    @Test
//    @DirtiesContext
//    public void findAllByBookerIdAndEndTimeIsBeforeOrderByStartTimeDescTest() {
//        User user = User.builder().name("name").email("mail@mail.ru").build();
//        User addedUser = userRepository.save(user);
//
//        Booking booking = Booking.builder()
//                .startTime(now().plusDays(3))
//                .endTime(now().minusDays(5))
//                .status(WAITING)
//                .booker(addedUser).build();
//        bookingRepository.save(booking);
//
//        List<Booking> result = bookingRepository.findAllByBookerIdAndEndTimeIsBeforeOrderByStartTimeDesc(
//                booking.getBooker().getId(), now());
//
//        assertEquals(result.get(0).getId(), 1L);
//        assertEquals(result.get(0).getBooker().getName(), addedUser.getName());
//        assertEquals(result.get(0).getBooker().getEmail(), addedUser.getEmail());
//        assertEquals(result.get(0).getStatus(), booking.getStatus());
//
//
//        assertEquals(result.get(0).getStartTime(), booking.getStartTime());
//        assertEquals(result.get(0).getEndTime(), booking.getEndTime());
//
//    }

//    @Test
//    @DirtiesContext
//    public void findBookingsByBookerIdAndStartTimeIsAfterOrderByStartTimeDescTest() {
//        User user = User.builder().name("name").email("mail@mail.ru").build();
//        User addedUser = userRepository.save(user);
//
//        Booking booking = Booking.builder()
//                .startTime(now().plusDays(3))
//                .endTime(now().minusDays(5))
//                .status(WAITING)
//                .booker(addedUser).build();
//        bookingRepository.save(booking);
//
//        List<Booking> result = bookingRepository.findBookingsByBookerIdAndStartTimeIsAfterOrderByStartTimeDesc(
//                booking.getBooker().getId(), now());
//
//        assertEquals(result.get(0).getId(), 1L);
//        assertEquals(result.get(0).getBooker().getName(), addedUser.getName());
//        assertEquals(result.get(0).getBooker().getEmail(), addedUser.getEmail());
//        assertEquals(result.get(0).getStatus(), booking.getStatus());
//
//
//        assertEquals(result.get(0).getStartTime(), booking.getStartTime());
//        assertEquals(result.get(0).getEndTime(), booking.getEndTime());
//
//    }


//    @Test
//    @DirtiesContext
//    public void findBookingsByBookerIdAndStatusEqualsTest() {
//        User user = User.builder().name("name").email("mail@mail.ru").build();
//        User addedUser = userRepository.save(user);
//
//        Booking booking = Booking.builder()
//                .startTime(now().plusDays(3))
//                .endTime(now().minusDays(5))
//                .status(WAITING)
//                .booker(addedUser).build();
//        bookingRepository.save(booking);
//
//        List<Booking> result = bookingRepository.findBookingsByBookerIdAndStatusEquals(
//                booking.getBooker().getId(), WAITING);
//
//        assertEquals(result.get(0).getId(), 1L);
//        assertEquals(result.get(0).getBooker().getName(), addedUser.getName());
//        assertEquals(result.get(0).getBooker().getEmail(), addedUser.getEmail());
//        assertEquals(result.get(0).getStatus(), booking.getStatus());
//
//
//        assertEquals(result.get(0).getStartTime(), booking.getStartTime());
//        assertEquals(result.get(0).getEndTime(), booking.getEndTime());
//
//    }

    @Test
    @DirtiesContext
    public void findBookingsByItemOwnerIdOrderByStartTimeDescTest() {
        User user = User.builder().name("name").email("mail@mail.ru").build();
        User addedUser = userRepository.save(user);

        ItemRequest request = ItemRequest.builder().requestor(addedUser).description("desc").creationTime(now()).build();
        ItemRequest addedRequest = requestRepository.save(request);

        Item item = Item.builder().name("name").description("desc").available(true).build();
        item.setOwner(addedUser);
        item.setRequest(addedRequest);
        itemRepository.save(item);

        Booking booking = Booking.builder()
                .startTime(now().plusDays(3))
                .endTime(now().minusDays(5))
                .status(WAITING)
                .item(item)
                .booker(addedUser).build();

        bookingRepository.save(booking);

        List<Booking> result = bookingRepository.findBookingsByItemOwnerIdOrderByStartTimeDesc(
                booking.getBooker().getId());

        assertEquals(result.get(0).getId(), 1L);
        assertEquals(result.get(0).getBooker().getName(), addedUser.getName());
        assertEquals(result.get(0).getBooker().getEmail(), addedUser.getEmail());
        assertEquals(result.get(0).getStatus(), booking.getStatus());


        assertEquals(result.get(0).getStartTime(), booking.getStartTime());
        assertEquals(result.get(0).getEndTime(), booking.getEndTime());

    }

    @Test
    @DirtiesContext
    public void findBookingsByItemOwnerIdAndStartTimeIsBeforeAndEndTimeIsAfterTest() {
        User user = User.builder().name("name").email("mail@mail.ru").build();
        User addedUser = userRepository.save(user);

        ItemRequest request = ItemRequest.builder().requestor(addedUser).description("desc").creationTime(now()).build();
        ItemRequest addedRequest = requestRepository.save(request);

        Item item = Item.builder().name("name").description("desc").available(true).build();
        item.setOwner(addedUser);
        item.setRequest(addedRequest);
        itemRepository.save(item);

        Booking booking = Booking.builder()
                .startTime(now().minusDays(3))
                .endTime(now().plusDays(5))
                .status(WAITING)
                .item(item)
                .booker(addedUser).build();

        bookingRepository.save(booking);

        List<Booking> result = bookingRepository.findBookingsByItemOwnerIdAndStartTimeIsBeforeAndEndTimeIsAfter(
                booking.getBooker().getId(), now(), now());

        assertEquals(result.get(0).getId(), 1L);
        assertEquals(result.get(0).getBooker().getName(), addedUser.getName());
        assertEquals(result.get(0).getBooker().getEmail(), addedUser.getEmail());
        assertEquals(result.get(0).getStatus(), booking.getStatus());


        assertEquals(result.get(0).getStartTime(), booking.getStartTime());
        assertEquals(result.get(0).getEndTime(), booking.getEndTime());

    }

    @Test
    @DirtiesContext
    public void findBookingsByItemOwnerIdAndEndTimeIsBeforeOrderByStartTimeDescTest() {
        User user = User.builder().name("name").email("mail@mail.ru").build();
        User addedUser = userRepository.save(user);

        ItemRequest request = ItemRequest.builder().requestor(addedUser).description("desc").creationTime(now()).build();
        ItemRequest addedRequest = requestRepository.save(request);

        Item item = Item.builder().name("name").description("desc").available(true).build();
        item.setOwner(addedUser);
        item.setRequest(addedRequest);
        itemRepository.save(item);

        Booking booking = Booking.builder()
                .startTime(now().minusDays(5))
                .endTime(now().minusDays(3))
                .status(WAITING)
                .item(item)
                .booker(addedUser).build();

        bookingRepository.save(booking);

        List<Booking> result = bookingRepository.findBookingsByItemOwnerIdAndEndTimeIsBeforeOrderByStartTimeDesc(
                booking.getBooker().getId(), now());

        assertEquals(result.get(0).getId(), 1L);
        assertEquals(result.get(0).getBooker().getName(), addedUser.getName());
        assertEquals(result.get(0).getBooker().getEmail(), addedUser.getEmail());
        assertEquals(result.get(0).getStatus(), booking.getStatus());


        assertEquals(result.get(0).getStartTime(), booking.getStartTime());
        assertEquals(result.get(0).getEndTime(), booking.getEndTime());

    }

    @Test
    @DirtiesContext
    public void findAllByItemOwnerIdTest() {
        User user = User.builder().name("name").email("mail@mail.ru").build();
        User addedUser = userRepository.save(user);

        ItemRequest request = ItemRequest.builder().requestor(addedUser).description("desc").creationTime(now()).build();
        ItemRequest addedRequest = requestRepository.save(request);

        Item item = Item.builder().name("name").description("desc").available(true).build();
        item.setOwner(addedUser);
        item.setRequest(addedRequest);
        itemRepository.save(item);

        Booking booking = Booking.builder()
                .startTime(now().plusDays(5))
                .endTime(now().plusDays(6))
                .status(WAITING)
                .item(item)
                .booker(addedUser).build();

        bookingRepository.save(booking);

        List<Booking> result = bookingRepository.findBookingsByItemOwnerIdAndStartTimeIsAfterOrderByStartTimeDesc(
                booking.getBooker().getId(), now());

        assertEquals(result.get(0).getId(), 1L);
        assertEquals(result.get(0).getBooker().getName(), addedUser.getName());
        assertEquals(result.get(0).getBooker().getEmail(), addedUser.getEmail());
        assertEquals(result.get(0).getStatus(), booking.getStatus());


        assertEquals(result.get(0).getStartTime(), booking.getStartTime());
        assertEquals(result.get(0).getEndTime(), booking.getEndTime());

    }

    @Test
    @DirtiesContext
    public void findBookingsByItemOwnerIdAndStatusEqualsTest() {
        User user = User.builder().name("name").email("mail@mail.ru").build();
        User addedUser = userRepository.save(user);

        ItemRequest request = ItemRequest.builder().requestor(addedUser).description("desc").creationTime(now()).build();
        ItemRequest addedRequest = requestRepository.save(request);

        Item item = Item.builder().name("name").description("desc").available(true).build();
        item.setOwner(addedUser);
        item.setRequest(addedRequest);
        itemRepository.save(item);

        Booking booking = Booking.builder()
                .startTime(now().plusDays(5))
                .endTime(now().plusDays(6))
                .status(WAITING)
                .item(item)
                .booker(addedUser).build();

        bookingRepository.save(booking);

        List<Booking> result = bookingRepository.findBookingsByItemOwnerIdAndStatusEquals(
                booking.getBooker().getId(), WAITING);

        assertEquals(result.get(0).getId(), 1L);
        assertEquals(result.get(0).getBooker().getName(), addedUser.getName());
        assertEquals(result.get(0).getBooker().getEmail(), addedUser.getEmail());
        assertEquals(result.get(0).getStatus(), booking.getStatus());


        assertEquals(result.get(0).getStartTime(), booking.getStartTime());
        assertEquals(result.get(0).getEndTime(), booking.getEndTime());

    }

    @Test
    @DirtiesContext
    public void findAllByItemIdTest() {
        User user = User.builder().name("name").email("mail@mail.ru").build();
        User addedUser = userRepository.save(user);

        ItemRequest request = ItemRequest.builder().requestor(addedUser).description("desc").creationTime(now()).build();
        ItemRequest addedRequest = requestRepository.save(request);

        Item item = Item.builder().name("name").description("desc").available(true).build();
        item.setOwner(addedUser);
        item.setRequest(addedRequest);
        itemRepository.save(item);

        Booking booking = Booking.builder()
                .startTime(now().plusDays(5))
                .endTime(now().plusDays(6))
                .status(WAITING)
                .item(item)
                .booker(addedUser).build();

        bookingRepository.save(booking);

        List<Booking> result = bookingRepository.findAllByItemId(
                booking.getItem().getId());

        assertEquals(result.get(0).getId(), 1L);
        assertEquals(result.get(0).getBooker().getName(), addedUser.getName());
        assertEquals(result.get(0).getBooker().getEmail(), addedUser.getEmail());
        assertEquals(result.get(0).getStatus(), booking.getStatus());


        assertEquals(result.get(0).getStartTime(), booking.getStartTime());
        assertEquals(result.get(0).getEndTime(), booking.getEndTime());

    }

    @Test
    @DirtiesContext
    public void findAllByItemIdAndStatus() {
        User user = User.builder().name("name").email("mail@mail.ru").build();
        User addedUser = userRepository.save(user);

        ItemRequest request = ItemRequest.builder().requestor(addedUser).description("desc").creationTime(now()).build();
        ItemRequest addedRequest = requestRepository.save(request);

        Item item = Item.builder().name("name").description("desc").available(true).build();
        item.setOwner(addedUser);
        item.setRequest(addedRequest);
        itemRepository.save(item);

        Booking booking = Booking.builder()
                .startTime(now().plusDays(5))
                .endTime(now().plusDays(6))
                .status(WAITING)
                .item(item)
                .booker(addedUser).build();

        bookingRepository.save(booking);

        List<Booking> result = bookingRepository.findAllByItemIdAndStatus(
                booking.getItem().getId(), WAITING);

        assertEquals(result.get(0).getId(), 1L);
        assertEquals(result.get(0).getBooker().getName(), addedUser.getName());
        assertEquals(result.get(0).getBooker().getEmail(), addedUser.getEmail());
        assertEquals(result.get(0).getStatus(), booking.getStatus());


        assertEquals(result.get(0).getStartTime(), booking.getStartTime());
        assertEquals(result.get(0).getEndTime(), booking.getEndTime());

    }

    @Test
    @DirtiesContext
    public void findAllByItemOwnerId() {
        User user = User.builder().name("name").email("mail@mail.ru").build();
        User addedUser = userRepository.save(user);

        ItemRequest request = ItemRequest.builder().requestor(addedUser).description("desc").creationTime(now()).build();
        ItemRequest addedRequest = requestRepository.save(request);

        Item item = Item.builder().name("name").description("desc").available(true).build();
        item.setOwner(addedUser);
        item.setRequest(addedRequest);
        itemRepository.save(item);

        Booking booking = Booking.builder()
                .startTime(now().plusDays(5))
                .endTime(now().plusDays(6))
                .status(WAITING)
                .item(item)
                .booker(addedUser).build();

        bookingRepository.save(booking);

        List<Booking> result = bookingRepository.findAllByItemOwnerId(
                booking.getItem().getOwner().getId());

        assertEquals(result.get(0).getId(), 1L);
        assertEquals(result.get(0).getBooker().getName(), addedUser.getName());
        assertEquals(result.get(0).getBooker().getEmail(), addedUser.getEmail());
        assertEquals(result.get(0).getStatus(), booking.getStatus());


        assertEquals(result.get(0).getStartTime(), booking.getStartTime());
        assertEquals(result.get(0).getEndTime(), booking.getEndTime());

    }

    @Test
    @DirtiesContext
    public void findAllByItem_OwnerIdAndStatusTest() {
        User user = User.builder().name("name").email("mail@mail.ru").build();
        User addedUser = userRepository.save(user);

        ItemRequest request = ItemRequest.builder().requestor(addedUser).description("desc").creationTime(now()).build();
        ItemRequest addedRequest = requestRepository.save(request);

        Item item = Item.builder().name("name").description("desc").available(true).build();
        item.setOwner(addedUser);
        item.setRequest(addedRequest);
        itemRepository.save(item);

        Booking booking = Booking.builder()
                .startTime(now().plusDays(5))
                .endTime(now().plusDays(6))
                .status(WAITING)
                .item(item)
                .booker(addedUser).build();

        bookingRepository.save(booking);

        List<Booking> result = bookingRepository.findAllByItem_OwnerIdAndStatus(
                booking.getItem().getOwner().getId(), WAITING);

        assertEquals(result.get(0).getId(), 1L);
        assertEquals(result.get(0).getBooker().getName(), addedUser.getName());
        assertEquals(result.get(0).getBooker().getEmail(), addedUser.getEmail());
        assertEquals(result.get(0).getStatus(), booking.getStatus());


        assertEquals(result.get(0).getStartTime(), booking.getStartTime());
        assertEquals(result.get(0).getEndTime(), booking.getEndTime());

    }

//    @Test
//    @DirtiesContext
//    public void findBookingByBookerIdOrderByStartTimeDescWithPageableTest() {
//        User user = User.builder().name("name").email("mail@mail.ru").build();
//        User addedUser = userRepository.save(user);
//
//        Booking booking = Booking.builder()
//                .endTime(now().plusDays(5))
//                .startTime(now().plusDays(3))
//                .status(WAITING)
//                .booker(addedUser).build();
//        bookingRepository.save(booking);
//
//        Page<Booking> resultList = bookingRepository.findBookingByBookerIdOrderByStartTimeDesc(
//                booking.getBooker().getId(), PageRequest.of(0, 10));
//        List<Booking> result = resultList.getContent();
//        assertEquals(result.get(0).getId(), 1L);
//        assertEquals(result.get(0).getBooker().getName(), addedUser.getName());
//        assertEquals(result.get(0).getBooker().getEmail(), addedUser.getEmail());
//        assertEquals(result.get(0).getStatus(), booking.getStatus());
//
//
//        assertEquals(result.get(0).getStartTime(), booking.getStartTime());
//        assertEquals(result.get(0).getEndTime(), booking.getEndTime());
//
//    }

}
