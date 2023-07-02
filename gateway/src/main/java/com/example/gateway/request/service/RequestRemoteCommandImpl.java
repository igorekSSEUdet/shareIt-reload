package com.example.gateway.request.service;

import com.example.gateway.client.BaseClient;
import com.example.gateway.request.dto.RequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Map;

import static com.example.gateway.utills.RestEndpoints.REQUEST_API_PREFIX;

@Service
public class RequestRemoteCommandImpl extends BaseClient implements RequestRemoteCommand {

    public RequestRemoteCommandImpl(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + REQUEST_API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    @Override
    public ResponseEntity<Object> createRequest(RequestDto requestDto, Long userId) {
        return post("", userId, requestDto);
    }

    @Override
    public ResponseEntity<Object> getAllOwnRequests(Long userId) {
        return get("", userId);
    }

    @Override
    public ResponseEntity<Object> getAllRequests(Long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/all?from={from}&size={size}", userId, parameters);
    }

    @Override
    public ResponseEntity<Object> getRequestById(Long userId, Long requestId) {
        return get("/" + requestId, userId);
    }
}
