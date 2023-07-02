package ru.practicum.shareit.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.ItemDtoMapper;

import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.now;

@Component
public class RequestDtoMapper {

    private final ItemRepository itemRepository;
    private final ItemDtoMapper itemDtoMapper;

    public RequestDtoMapper(ItemRepository itemRepository, ItemDtoMapper itemDtoMapper) {
        this.itemRepository = itemRepository;
        this.itemDtoMapper = itemDtoMapper;
    }

    public ItemRequest toRequest(RequestDto requestDto) {
        return ItemRequest.builder()
                .description(requestDto.getDescription())
                .creationTime(now())
                .build();
    }

    public ItemRequestDto toRequestDto(ItemRequest request) {
        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreationTime())
                .items(itemDtoMapper.toItemDto(itemRepository.findAllByRequestId(request.getId())))
                .build();
    }

    public List<ItemRequestDto> toListOfDto(List<ItemRequest> request) {

        List<ItemRequestDto> itemRequestDto = new ArrayList<>();
        for (ItemRequest requests : request) {
            itemRequestDto.add(toRequestDto(requests));

        }

        return itemRequestDto;
    }
}
