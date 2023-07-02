package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingDtoMapper;
import ru.practicum.shareit.item.dto.DetailedItemDto;
import ru.practicum.shareit.item.dto.ItemCreationRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.storage.RequestRepository;
import ru.practicum.shareit.user.mapper.CommentDtoMapper;
import ru.practicum.shareit.user.mapper.ItemDtoMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.service.ItemService.*;
import static ru.practicum.shareit.user.service.UserService.checkUserExistsById;

@Service
@Slf4j
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemDtoMapper itemDtoMapper;
    private final BookingDtoMapper bookingDtoMapper;
    private final CommentDtoMapper commentDtoMapper;
    private final RequestRepository requestRepository;

    public ItemServiceImpl(ItemRepository itemRepository,
                           UserRepository userRepository,
                           ItemDtoMapper itemDtoMapper,
                           BookingDtoMapper bookingDtoMapper,
                           CommentDtoMapper commentDtoMapper, RequestRepository requestRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.itemDtoMapper = itemDtoMapper;
        this.bookingDtoMapper = bookingDtoMapper;
        this.commentDtoMapper = commentDtoMapper;
        this.requestRepository = requestRepository;
    }


    @Override
    public ItemDto addItem(ItemCreationRequestDto itemDto, Long ownerId) {
        checkUserExistsById(userRepository, ownerId);
        if (itemDto.getRequestId() == null) {
            Item item = itemDtoMapper.toItem(itemDto, ownerId);
            Item addedItem = itemRepository.save(item);
            log.debug("Item ID_{} added.", addedItem.getId());
            return itemDtoMapper.toItemDto(addedItem);
        } else return addItemOnRequest(itemDto, ownerId);
    }

    private ItemDto addItemOnRequest(ItemCreationRequestDto itemDto, Long ownerId) {
        checkHasRequest(requestRepository, itemDto.getRequestId());
        checkUserExistsById(userRepository, ownerId);
        Item item = itemDtoMapper.toItem(itemDto, ownerId);
        Item addedItem = itemRepository.save(item);
        log.debug("Item ID_{} added.", addedItem.getId());
        return itemDtoMapper.toItemDto(addedItem);

    }

    @Override
    public ItemDto updateItem(ItemUpdateRequestDto itemDto, Long itemId, Long userId) {
        checkItemExistsById(itemRepository, itemId);
        checkUserExistsById(userRepository, userId);
        checkOwnerOfItemByItemIdAndUserId(itemRepository, itemId, userId);
        Item item = itemDtoMapper.toItem(itemDto, itemId, userId);
        Item updatedItem = itemRepository.save(item);
        log.debug("Item ID_{} updated.", itemId);
        return itemDtoMapper.toItemDto(updatedItem);
    }

    @Override
    @Transactional(readOnly = true)
    public DetailedItemDto getItemByItemId(Long itemId, Long userId) {
        checkItemExistsById(itemRepository, itemId);
        Item item = itemRepository.findById(itemId).get();
        log.debug("Item ID_{} returned.", item.getId());
        if (isOwner(item, userId)) {
            return itemDtoMapper.toDetailedItemDtoForOwner(item, commentDtoMapper, bookingDtoMapper);
        }
        return itemDtoMapper.toDetailedItemDto(item, commentDtoMapper);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetailedItemDto> getItemsByOwnerId(Long ownerId, Integer from, Integer size) {
        if (!isRequestWithPagination(from, size)) {
            List<Item> items = itemRepository.findByOwner_Id(ownerId);
            log.debug("All items have been returned, {} in total.", items.size());
            return itemDtoMapper.toDetailedItemDto(items, commentDtoMapper, bookingDtoMapper);
        }
        return getItemsByOwnerIdWithPagination(ownerId, from, size);
    }

    private List<DetailedItemDto> getItemsByOwnerIdWithPagination(Long ownerId, Integer from, Integer size) {
        List<Item> items = itemRepository.findByOwner_Id(ownerId, PageRequest.of(from, size)).getContent();
        log.debug("All items have been returned, {} in total.", items.size());
        return itemDtoMapper.toDetailedItemDto(items, commentDtoMapper, bookingDtoMapper);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> searchItemsByNameOrDescription(String text, Integer from, Integer size) {
        if (StringUtils.isEmpty(text)) {
            return Collections.emptyList();
        }
        if (!isRequestWithPagination(from, size)) {
            List<Item> foundItems = itemRepository.findByAvailableIsTrue()
                    .stream()
                    .filter(itemDto -> StringUtils.containsIgnoreCase(itemDto.getName(), text)
                            || StringUtils.containsIgnoreCase(itemDto.getDescription(), text))
                    .collect(Collectors.toList());
            log.debug("Returned items containing '{}', {} in total.", text, foundItems.size());

            return itemDtoMapper.toItemDto(foundItems);
        }
        return searchItemsByNameOrDescriptionWithPagination(text, from, size);
    }

    private List<ItemDto> searchItemsByNameOrDescriptionWithPagination(String text, Integer from, Integer size) {
        List<Item> foundItems = itemRepository.findByAvailableIsTrue()
                .stream()
                .filter(itemDto -> StringUtils.containsIgnoreCase(itemDto.getName(), text)
                        || StringUtils.containsIgnoreCase(itemDto.getDescription(), text))
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());
        return itemDtoMapper.toItemDto(foundItems);
    }

    private boolean isRequestWithPagination(Integer from, Integer size) {
        return from != null && size != null;
    }
}
