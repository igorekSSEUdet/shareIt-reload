package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.mapper.RequestDtoMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.ItemDtoMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestDtoMapperTest {

    @InjectMocks
    private RequestDtoMapper mapper;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemDtoMapper itemDtoMapper;

    @Test
    public void toRequestTest() {
        RequestDto requestDto = RequestDto.builder()
                .description("desc")
                .build();
        ItemRequest itemRequest = mapper.toRequest(requestDto);
        assertEquals(itemRequest.getDescription(), requestDto.getDescription());
    }

    @Test
    public void toRequestDtoTest() {
        ItemRequest request = ItemRequest.builder().id(1L).creationTime(now()).description("desc").build();
        Item item = Item.builder().id(1L).description("desc").name("name").build();
        List<Item> items = new ArrayList<>(Collections.singleton(item));
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(items);
        when(itemDtoMapper.toItemDto(anyCollection())).thenReturn(Collections.singletonList(ItemDto
                .builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription()).build()));
        ItemRequestDto dto = mapper.toRequestDto(request);
        assertEquals(dto.getId(), item.getId());
        assertEquals(dto.getDescription(), item.getDescription());
    }

    @Test
    public void toListOfDtoTest() {
        ItemRequest request = ItemRequest.builder().id(1L).description("desc").build();
        List<ItemRequest> itemRequestList = Collections.singletonList(request);

        List<ItemRequestDto> result = mapper.toListOfDto(itemRequestList);
        assertEquals(result.get(0).getDescription(), request.getDescription());
        assertEquals(result.get(0).getId(), request.getId());

    }
}
