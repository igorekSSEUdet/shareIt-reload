package com.example.gateway.item.service;

import com.example.gateway.client.BaseClient;
import com.example.gateway.item.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Map;

import static com.example.gateway.utills.RestEndpoints.ITEM_API_PREFIX;

@Service
public class ItemRemoteCommandImpl extends BaseClient implements ItemRemoteCommand {

    public ItemRemoteCommandImpl(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + ITEM_API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    @Override
    public ResponseEntity<Object> addItem(Long ownerId, ItemCreationRequestDto itemDto) {
        return post("", ownerId, itemDto);
    }

    @Override
    public ResponseEntity<Object> updateItem(Long userId, Long itemId, ItemUpdateRequestDto itemDto) {
        return patch("/" + itemId, userId, itemDto);
    }

    @Override
    public ResponseEntity<Object> getItemById(Long userId, Long itemId) {
        return get("/" + itemId, userId);

    }

    @Override
    public ResponseEntity<Object> getItemsByOwnerId(Long ownerId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", ownerId, parameters);
    }

    @Override
    public ResponseEntity<Object> searchItemsByNameOrDescription(String text, Integer from, Integer size, Long ownerId) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", ownerId, parameters);
    }

    @Override
    public ResponseEntity<Object> addComment(Long userId, RequestCommentDto comment, Long itemId) {
        return post("/" + itemId + "/comment", userId, comment);

    }
}
