package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.exceptions.RequestNotFoundException;
import ru.practicum.shareit.request.storage.RequestRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;


public interface RequestService {

    static void checkUserExistsById(UserRepository userRepository, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw UserNotFoundException.getFromUserId(userId);
        }
    }

    static void checkRequestExistsById(RequestRepository requestRepository, Long requestId) {
        if (!requestRepository.existsById(requestId)) {
            throw RequestNotFoundException.getFromRequestId(requestId);
        }
    }


    ItemRequestDto createRequest(RequestDto requestDto, Long userId);

    List<ItemRequestDto> getAllOwnRequests(Long userId);

    List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size);

    ItemRequestDto getRequestById(Long requestId, Long userId);
}
