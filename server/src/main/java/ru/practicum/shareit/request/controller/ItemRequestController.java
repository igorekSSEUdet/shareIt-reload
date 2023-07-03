package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.utills.UserHttpHeaders;

import javax.validation.Valid;
import java.util.List;


@RestController
@Slf4j
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final RequestService service;

    @PostMapping
    public ItemRequestDto createRequest(@RequestBody RequestDto requestDto,
                                        @RequestHeader(UserHttpHeaders.USER_ID) Long userId) {
        log.info("Received a POST request for the endpoint /requests with userId_{}", userId);
        return service.createRequest(requestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getAllOwnRequests(@RequestHeader(UserHttpHeaders.USER_ID) Long userId) {
        log.info("Received a GET request for the endpoint /requests with userId_{}", userId);
        return service.getAllOwnRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(UserHttpHeaders.USER_ID) Long userId,
                                               @RequestParam(required = false) Integer from,
                                               @RequestParam(required = false) Integer size) {
        log.info("Received a GET request for the endpoint /requests/all with userId_{}", userId);
        return service.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(UserHttpHeaders.USER_ID) Long userId,
                                         @PathVariable Long requestId) {
        log.info("Received a GET request for the endpoint /requests/{requestId} with userId_{}", userId);
        return service.getRequestById(requestId, userId);
    }
}
