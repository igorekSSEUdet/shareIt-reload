package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.mapper.RequestDtoMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.RequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static ru.practicum.shareit.request.service.RequestService.checkRequestExistsById;
import static ru.practicum.shareit.request.service.RequestService.checkUserExistsById;

@Service
public class RequestServiceImpl implements RequestService {

    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    private final RequestDtoMapper requestDtoMapper;

    @Autowired
    public RequestServiceImpl(UserRepository userRepository, RequestRepository requestRepository, RequestDtoMapper requestDtoMapper) {
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.requestDtoMapper = requestDtoMapper;
    }

    @Override
    public ItemRequestDto createRequest(RequestDto requestDto, Long userId) {
        checkUserExistsById(userRepository, userId);
        ItemRequest saveRequest = requestDtoMapper.toRequest(requestDto);
        saveRequest.setRequestor(userRepository.findById(userId).orElseThrow());
        return requestDtoMapper.toRequestDto(requestRepository.save(saveRequest));
    }

    @Override
    public List<ItemRequestDto> getAllOwnRequests(Long userId) {
        checkUserExistsById(userRepository, userId);
        return requestDtoMapper.toListOfDto(requestRepository.findAllByRequestorId(userId));
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size) {
        checkUserExistsById(userRepository, userId);
        return getAllByParam(userId, from, size);
    }

    @Override
    public ItemRequestDto getRequestById(Long requestId, Long userId) {
        checkUserExistsById(userRepository, userId);
        checkRequestExistsById(requestRepository, requestId);
        return requestDtoMapper.toRequestDto(requestRepository.findById(requestId).orElseThrow());
    }

    private List<ItemRequestDto> getAllByParam(Long userId, Integer from, Integer size) {
        if (!(from == null) || !(size == null)) {
            return getAllRequestsWithPagination(userId, from, size);
        } else {
            return getAllRequestsWithoutPagination(userId);
        }
    }

    private List<ItemRequestDto> getAllRequestsWithPagination(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        Page<ItemRequest> page = requestRepository.findAllByRequestor_IdNot(userId, pageable);

        return requestDtoMapper.toListOfDto(page.getContent());
    }

    private List<ItemRequestDto> getAllRequestsWithoutPagination(Long userId) {
        return requestDtoMapper.toListOfDto(requestRepository.findAllByRequestorIdOrderByCreationTimeDesc(userId));
    }
}
