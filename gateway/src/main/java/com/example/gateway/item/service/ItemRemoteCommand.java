package com.example.gateway.item.service;

import com.example.gateway.item.dto.*;
import org.springframework.http.ResponseEntity;

public interface ItemRemoteCommand {
    ResponseEntity<Object> addItem(Long ownerId, ItemCreationRequestDto itemDto);

    ResponseEntity<Object> updateItem(Long userId, Long itemId, ItemUpdateRequestDto itemDto);

    ResponseEntity<Object> getItemById(Long userId, Long itemId);

    ResponseEntity<Object> getItemsByOwnerId(Long ownerId, Integer from, Integer size);

    ResponseEntity<Object> searchItemsByNameOrDescription(String text, Integer from, Integer size, Long ownerId);

    ResponseEntity<Object> addComment(Long userId, RequestCommentDto comment, Long itemId);

}
