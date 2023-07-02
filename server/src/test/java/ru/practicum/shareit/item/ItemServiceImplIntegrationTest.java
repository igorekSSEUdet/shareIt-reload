package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.dto.DetailedItemDto;
import ru.practicum.shareit.item.dto.ItemCreationRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserCreationRequestDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ItemServiceImplIntegrationTest {


    private final ItemCreationRequestDto itemDto = ItemCreationRequestDto.builder().available(true).name("name")
            .description("desc").build();
    private final UserCreationRequestDto userRequest = UserCreationRequestDto.builder().name("name").email("mail@mail.ru").build();
    @Autowired
    UserServiceImpl userService;
    @Autowired
    private ItemServiceImpl itemService;

    public ItemServiceImplIntegrationTest() {
    }

    @Test
    @DirtiesContext
    public void addItemTest() {
        userService.addUser(userRequest);
        ItemDto resultItemDto = itemService.addItem(itemDto, 1L);
        assertEquals(resultItemDto.getDescription(), itemDto.getDescription());
        assertEquals(resultItemDto.getName(), itemDto.getName());
        assertEquals(resultItemDto.getAvailable(), itemDto.getAvailable());

    }

    @Test
    @DirtiesContext
    public void updateItemTest() {
        userService.addUser(userRequest);
        itemService.addItem(itemDto, 1L);
        ItemUpdateRequestDto updateItem = ItemUpdateRequestDto.builder()
                .name("update").description("update").build();
        ItemDto resultItemDto = itemService.updateItem(updateItem, 1L, 1L);

        assertEquals(resultItemDto.getDescription(), updateItem.getDescription().get());
        assertEquals(resultItemDto.getName(), updateItem.getName().get());

    }

    @Test
    @DirtiesContext
    public void getItemByIdTest() {
        userService.addUser(userRequest);
        itemService.addItem(itemDto, 1L);
        DetailedItemDto resultItemDto = itemService.getItemByItemId(1L, 1L);
        assertEquals(resultItemDto.getDescription(), itemDto.getDescription());
        assertEquals(resultItemDto.getName(), itemDto.getName());
        assertEquals(resultItemDto.getAvailable(), itemDto.getAvailable());

    }


}
