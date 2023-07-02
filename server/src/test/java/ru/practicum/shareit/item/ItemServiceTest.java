package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.mapper.BookingDtoMapper;
import ru.practicum.shareit.item.dto.DetailedItemDto;
import ru.practicum.shareit.item.dto.ItemCreationRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.RequestRepository;
import ru.practicum.shareit.user.mapper.CommentDtoMapper;
import ru.practicum.shareit.user.mapper.ItemDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemDtoMapper itemDtoMapper;
    @Mock
    private BookingDtoMapper bookingDtoMapper;
    @Mock
    private CommentDtoMapper commentDtoMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RequestRepository requestRepository;

    private ItemService itemService;

    @BeforeEach
    public void setUp() {
        this.itemService = new ItemServiceImpl(itemRepository, userRepository, itemDtoMapper, bookingDtoMapper, commentDtoMapper, requestRepository);
    }

    @Test
    public void testAddItem() {

        Long ownerId = 1L;
        ItemCreationRequestDto itemDto = new ItemCreationRequestDto();
        itemDto.setName("Test item");
        itemDto.setDescription("Test description");
        itemDto.setRequestId(null);

        User owner = new User();
        owner.setId(ownerId);

        Item item = new Item();
        item.setId(1L);
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setOwner(owner);

        ItemDto itemDtoResult = new ItemDto();
        itemDtoResult.setId(1L);
        itemDtoResult.setName(itemDto.getName());
        itemDtoResult.setDescription(itemDto.getDescription());

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.save(any())).thenReturn(item);
        when(itemDtoMapper.toItemDto(item)).thenReturn(itemDtoResult);


        ItemDto createdItem = itemService.addItem(itemDto, ownerId);

        assertEquals(createdItem.getId(), itemDtoResult.getId());
        assertEquals(createdItem.getName(), itemDtoResult.getName());
        assertEquals(createdItem.getDescription(), itemDtoResult.getDescription());

        verify(itemDtoMapper, times(1)).toItemDto(item);
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    public void updateItemTest() {

        ItemUpdateRequestDto itemDto = new ItemUpdateRequestDto();
        itemDto.setAvailable(true);
        itemDto.setName("name");
        itemDto.setDescription("desc");

        User user = new User();
        user.setId(1L);

        Item item = new Item();
        item.setAvailable(true);
        item.setName("name");
        item.setDescription("desc");
        item.setOwner(user);
        item.setId(1L);

        when(itemRepository.save(item)).thenReturn(item);

        Item updatedItem = itemRepository.save(item);
        System.out.println(updatedItem);
        assertEquals(updatedItem.getName(), item.getName());
    }


    @Test
    public void getItemByItemIdTest() {

        when(itemRepository.existsById(anyLong())).thenReturn(true);
        User user = new User();
        user.setId(2L);

        Item item = new Item();
        item.setId(1L);
        item.setName("name");
        item.setAvailable(true);
        item.setDescription("desc");
        item.setOwner(user);
        item.setId(1L);

        DetailedItemDto dto = new DetailedItemDto();
        dto.setAvailable(item.getAvailable());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(itemService.getItemByItemId(item.getId(), user.getId())).thenReturn(dto);

        DetailedItemDto updatedItem = itemService.getItemByItemId(item.getId(), user.getId());
        assertEquals(updatedItem.getName(), dto.getName());

    }

    @Test
    public void testGetItemsByOwnerId() {
        Long ownerId = 1L;

        User user = User.builder().id(1L).build();
        User user2 = User.builder().id(2L).build();

        Item item1 = new Item();
        item1.setId(1L);
        item1.setOwner(user);
        item1.setName("Item1");
        Item item2 = new Item();
        item2.setId(2L);
        item2.setOwner(user2);
        item2.setName("Item2");

        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        DetailedItemDto detailedItemDto1 = new DetailedItemDto();
        detailedItemDto1.setId(1L);
        detailedItemDto1.setName("Item1");
        DetailedItemDto detailedItemDto2 = new DetailedItemDto();
        detailedItemDto2.setId(2L);
        detailedItemDto2.setName("Item2");

        List<DetailedItemDto> expectedItemList = new ArrayList<>();
        expectedItemList.add(detailedItemDto1);
        expectedItemList.add(detailedItemDto2);

        when(itemRepository.findByOwner_Id(ownerId)).thenReturn(items);
        when(itemDtoMapper.toDetailedItemDto(anyList(), eq(commentDtoMapper), eq(bookingDtoMapper)))
                .thenReturn(expectedItemList);

        List<DetailedItemDto> itemList = itemService.getItemsByOwnerId(ownerId, null, null);

        assertEquals(expectedItemList, itemList);
        verify(itemRepository, times(1)).findByOwner_Id(ownerId);
        verify(itemDtoMapper, times(1)).toDetailedItemDto(eq(items), eq(commentDtoMapper),
                eq(bookingDtoMapper));
    }


    @Test
    public void testSearchItemsByNameOrDescription() {
        String searchText = "test";
        List<Item> itemList = new ArrayList<>();
        itemList.add(Item.builder().id(1L).available(true).name("test").description("desc").build());
        itemList.add(Item.builder().id(2L).available(true).name("name").description("test").build());

        List<ItemDto> itemDto = new ArrayList<>();
        itemDto.add(ItemDto.builder().id(1L).available(true).name("test").description("desc").build());
        itemDto.add(ItemDto.builder().id(1L).available(true).name("test").description("desc").build());


        when(itemRepository.findByAvailableIsTrue()).thenReturn(itemList);
        when(itemDtoMapper.toItemDto(itemList)).thenReturn(itemDto);

        List<ItemDto> result = itemService.searchItemsByNameOrDescription(searchText, null, null);

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        verify(itemRepository).findByAvailableIsTrue();
        verify(itemDtoMapper).toItemDto(itemList);
    }


    @Test
    public void testAddItemOnRequest() {

        Long ownerId = 1L;
        ItemCreationRequestDto itemDto = new ItemCreationRequestDto();
        itemDto.setName("Test item");
        itemDto.setDescription("Test description");
        itemDto.setRequestId(1L);

        User owner = new User();
        owner.setId(ownerId);

        Item item = new Item();
        item.setId(1L);
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setOwner(owner);

        ItemDto itemDtoResult = new ItemDto();
        itemDtoResult.setId(1L);
        itemDtoResult.setName(itemDto.getName());
        itemDtoResult.setDescription(itemDto.getDescription());

        ItemRequest request = ItemRequest.builder().id(1L).build();

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemDtoMapper.toItem(eq(itemDto), anyLong())).thenReturn(item);
        when(itemRepository.save(any())).thenReturn(item);
        when(requestRepository.findById(request.getId())).thenReturn(Optional.of(request));
        when(itemDtoMapper.toItemDto(item)).thenReturn(itemDtoResult);

        ItemDto createdItem = itemService.addItem(itemDto, ownerId);
        assertEquals(createdItem.getId(), item.getId());
        assertEquals(createdItem.getName(), item.getName());
        assertEquals(createdItem.getDescription(), item.getDescription());

    }

}

