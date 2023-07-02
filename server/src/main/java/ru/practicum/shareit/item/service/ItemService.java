package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.DetailedItemDto;
import ru.practicum.shareit.item.dto.ItemCreationRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.exceptions.RequestNotFoundException;
import ru.practicum.shareit.request.storage.RequestRepository;

import java.util.List;

public interface ItemService {
    static void checkItemExistsById(ItemRepository itemRepository, Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw ItemNotFoundException.getFromItemId(itemId);
        }
    }

    static boolean isOwner(Item item, Long userId) {
        Long ownerId = item.getOwner().getId();
        return ownerId.equals(userId);
    }

    static void checkOwnerOfItemByItemIdAndUserId(ItemRepository itemRepository,
                                                  Long itemId, Long userId) {
        Long ownerId = itemRepository.getReferenceById(itemId).getOwner().getId();
        if (!ownerId.equals(userId)) {
            throw ItemNotFoundException.getFromItemIdAndUserId(itemId, userId);
        }
    }

    static void checkHasRequest(RequestRepository repository, Long requestId) {
        if (repository.findById(requestId).isEmpty()) throw new RequestNotFoundException("Request not found error");
    }

    ItemDto addItem(ItemCreationRequestDto itemDto, Long ownerId);

    ItemDto updateItem(ItemUpdateRequestDto itemDto, Long itemId, Long ownerId);

    DetailedItemDto getItemByItemId(Long itemId, Long userId);

    List<DetailedItemDto> getItemsByOwnerId(Long ownerId, Integer from, Integer size);

    List<ItemDto> searchItemsByNameOrDescription(String text, Integer from, Integer size);
}
