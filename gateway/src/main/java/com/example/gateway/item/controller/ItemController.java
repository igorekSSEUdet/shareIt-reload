package com.example.gateway.item.controller;

import com.example.gateway.item.dto.*;
import com.example.gateway.item.service.ItemRemoteCommand;
import com.example.gateway.utills.UserHttpHeaders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@Slf4j
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemController {


    private final ItemRemoteCommand command;

    @PostMapping
    public HttpEntity<Object> addItem(@RequestHeader(UserHttpHeaders.USER_ID) Long ownerId,
                                      @RequestBody @Valid ItemCreationRequestDto itemDto) {
        log.info("Received a POST request for the endpoint /items with userId_{}", ownerId);
        return command.addItem(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(UserHttpHeaders.USER_ID) Long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody ItemUpdateRequestDto itemDto) {
        log.info("Received a PATCH request for the endpoint /items/{itemId} with userId_{}", userId);
        return command.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(UserHttpHeaders.USER_ID) Long userId,
                                              @PathVariable Long itemId) {
        log.info("Received a GET request for the endpoint /items/{itemId} with userId_{}", userId);
        return command.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByOwnerId(@RequestHeader(UserHttpHeaders.USER_ID) Long ownerId,
                                                    @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                    @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Received a GET request for the endpoint /items with userId_{}", ownerId);
        return command.getItemsByOwnerId(ownerId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemsByNameOrDescription(@RequestHeader(UserHttpHeaders.USER_ID) Long ownerId,
                                                                 @RequestParam String text, @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                                 @Positive @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        log.info("Received a GET request for the endpoint /items/search");
        return command.searchItemsByNameOrDescription(text, from, size, ownerId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(UserHttpHeaders.USER_ID) Long userId,
                                             @RequestBody @Valid RequestCommentDto comment,
                                             @PathVariable Long itemId) {
        log.info("Received a POST request for the endpoint /items/{itemId}/comment with userId_{}", userId);
        return command.addComment(userId, comment, itemId);
    }
}
