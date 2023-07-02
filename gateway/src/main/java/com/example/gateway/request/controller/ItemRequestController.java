package com.example.gateway.request.controller;

import com.example.gateway.request.dto.RequestDto;
import com.example.gateway.request.service.RequestRemoteCommand;
import com.example.gateway.utills.UserHttpHeaders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@Slf4j
@RequestMapping(path = "/requests")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemRequestController {


    private final RequestRemoteCommand command;

    @PostMapping
    public HttpEntity<Object> createRequest(@RequestBody @Valid RequestDto requestDto,
                                            @RequestHeader(UserHttpHeaders.USER_ID) Long userId) {
        log.info("Received a POST request for the endpoint /requests with userId_{}", userId);
        return command.createRequest(requestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllOwnRequests(@RequestHeader(UserHttpHeaders.USER_ID) Long userId) {
        log.info("Received a GET request for the endpoint /requests with userId_{}", userId);
        return command.getAllOwnRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(UserHttpHeaders.USER_ID) Long userId,
                                                 @RequestParam(required = false, defaultValue = "0") Integer from,
                                                 @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Received a GET request for the endpoint /requests/all with userId_{}", userId);
        return command.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(UserHttpHeaders.USER_ID) Long userId,
                                                 @PathVariable Long requestId) {
        log.info("Received a GET request for the endpoint /requests/{requestId} with userId_{}", userId);
        return command.getRequestById(userId, requestId);
    }
}
