package ru.practicum.shareit.dataJpaTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Test
    @DirtiesContext
    public void findByOwnerIdTest() {

        User user = User.builder().name("name").email("mail@mail.ru").build();
        User addedUser = userRepository.save(user);

        ItemRequest request = ItemRequest.builder().requestor(addedUser).description("desc").creationTime(now()).build();
        ItemRequest addedRequest = requestRepository.save(request);

        Item item = Item.builder().name("name").description("desc").available(true).build();
        item.setOwner(addedUser);
        item.setRequest(addedRequest);
        itemRepository.save(item);

        List<Item> result = itemRepository.findByOwner_Id(item.getOwner().getId());
        assertEquals(result.get(0).getName(), item.getName());
        assertEquals(result.get(0).getDescription(), item.getDescription());
        assertEquals(result.get(0).getAvailable(), item.getAvailable());
        assertEquals(result.get(0).getOwner(), addedUser);
        assertEquals(result.get(0).getRequest(), addedRequest);

    }

    @Test
    @DirtiesContext
    public void findByAvailableIsTrueTest() {

        User user = User.builder().name("name").email("mail@mail.ru").build();
        User addedUser = userRepository.save(user);

        ItemRequest request = ItemRequest.builder().requestor(addedUser).description("desc").creationTime(now()).build();
        ItemRequest addedRequest = requestRepository.save(request);

        Item item = Item.builder().name("name").description("desc").available(true).build();
        item.setOwner(addedUser);
        item.setRequest(addedRequest);
        itemRepository.save(item);
        List<Item> result = itemRepository.findByAvailableIsTrue();
        assertEquals(result.get(0).getName(), item.getName());
        assertEquals(result.get(0).getDescription(), item.getDescription());
        assertEquals(result.get(0).getAvailable(), item.getAvailable());
        assertEquals(result.get(0).getOwner(), addedUser);
        assertEquals(result.get(0).getRequest(), addedRequest);

    }

    @Test
    @DirtiesContext
    public void findAllByRequestIdTest() {

        User user = User.builder().name("name").email("mail@mail.ru").build();
        User addedUser = userRepository.save(user);

        ItemRequest request = ItemRequest.builder().requestor(addedUser).description("desc").creationTime(now()).build();
        ItemRequest addedRequest = requestRepository.save(request);

        Item item = Item.builder().name("name").description("desc").available(true).build();
        item.setOwner(addedUser);
        item.setRequest(addedRequest);
        itemRepository.save(item);

        List<Item> result = itemRepository.findAllByRequestId(item.getRequest().getId());
        assertEquals(result.get(0).getName(), item.getName());
        assertEquals(result.get(0).getDescription(), item.getDescription());
        assertEquals(result.get(0).getAvailable(), item.getAvailable());
        assertEquals(result.get(0).getOwner(), addedUser);
        assertEquals(result.get(0).getRequest(), addedRequest);

    }


}
