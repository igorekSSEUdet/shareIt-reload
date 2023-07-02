package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.comment.CommentService;
import ru.practicum.shareit.utills.UserHttpHeaders;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final CommentService commentService;

    @PostMapping
    public ItemDto addItem(@RequestHeader(UserHttpHeaders.USER_ID) Long ownerId,
                           @RequestBody ItemCreationRequestDto itemDto) {
        log.info("Received a POST request for the endpoint /items with userId_{}", ownerId);
        return itemService.addItem(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(UserHttpHeaders.USER_ID) Long userId,
                              @PathVariable Long itemId,
                              @RequestBody ItemUpdateRequestDto itemDto) {
        log.info("Received a PATCH request for the endpoint /items/{itemId} with userId_{}", userId);
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public DetailedItemDto getItemById(@RequestHeader(UserHttpHeaders.USER_ID) Long userId,
                                       @PathVariable Long itemId) {
        log.info("Received a GET request for the endpoint /items/{itemId} with userId_{}", userId);
        return itemService.getItemByItemId(itemId, userId);
    }

    @GetMapping
    public List<DetailedItemDto> getItemsByOwnerId(@RequestHeader(UserHttpHeaders.USER_ID) Long ownerId,
                                                   @RequestParam(required = false) Integer from,
                                                   @RequestParam(required = false) Integer size) {
        log.info("Received a GET request for the endpoint /items with userId_{}", ownerId);
        return itemService.getItemsByOwnerId(ownerId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemsByNameOrDescription(@RequestParam String text,
                                                        @RequestParam(required = false) Integer from,
                                                        @RequestParam(required = false) Integer size) {
        log.info("Received a GET request for the endpoint /items/search");
        return itemService.searchItemsByNameOrDescription(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(UserHttpHeaders.USER_ID) Long userId,
                                 @RequestBody RequestCommentDto comment,
                                 @PathVariable Long itemId) {
        log.info("Received a POST request for the endpoint /items/{itemId}/comment with userId_{}", userId);
        return commentService.addComment(comment, userId, itemId);
    }
}
