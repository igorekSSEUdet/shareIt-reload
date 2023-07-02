package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.mapper.RequestDtoMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.request.storage.RequestRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private RequestDtoMapper requestDtoMapper;

    private RequestService requestService;

    @BeforeEach
    public void setUp() {
        this.requestService = new RequestServiceImpl(userRepository, requestRepository, requestDtoMapper);
    }

    @Test
    void createRequestTest() {

        LocalDateTime now = now();
        RequestDto requestDto = RequestDto.builder().description("desc").build();

        ItemRequest saveRequest = ItemRequest.builder().id(1L).description("desc")
                .creationTime(now).build();
        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(requestDtoMapper.toRequest(requestDto))
                .thenReturn(saveRequest);

        User user = User.builder().id(1L).name("name").email("mail@mail.ru").build();

        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));

        when(requestRepository.save(any()))
                .thenReturn(saveRequest);

        ItemRequestDto dto = ItemRequestDto.builder().id(saveRequest.getId())
                .description(saveRequest.getDescription()).created(saveRequest.getCreationTime()).build();
        when(requestDtoMapper.toRequestDto(any()))
                .thenReturn(dto);

        ItemRequestDto testDto = requestService.createRequest(requestDto, 1L);

        assertEquals(dto.getId(), testDto.getId());
        assertEquals(dto.getDescription(), testDto.getDescription());
        assertEquals(dto.getCreated(), testDto.getCreated());

        verify(requestDtoMapper, times(1)).toRequest(any());
        verify(requestDtoMapper, times(1)).toRequestDto(any());
        verify(userRepository, times(1)).findById(any());
        verify(requestRepository, times(1)).save(any());

    }


    @Test
    void getAllOwnRequestsTest() {

        LocalDateTime now = now();
        ItemRequest saveRequest = ItemRequest.builder().id(1L).description("desc")
                .creationTime(now).build();
        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(requestRepository.findAllByRequestorId(anyLong()))
                .thenReturn(List.of(saveRequest));

        ItemRequestDto dto = ItemRequestDto.builder()
                .id(saveRequest.getId())
                .description(saveRequest.getDescription())
                .created(saveRequest.getCreationTime()).build();

        when(requestDtoMapper.toListOfDto(any()))
                .thenReturn(List.of(dto));

        List<ItemRequestDto> result = requestService.getAllOwnRequests(1L);

        assertEquals(result.get(0).getId(), dto.getId());
        assertEquals(result.get(0).getDescription(), dto.getDescription());
        assertEquals(result.get(0).getCreated(), dto.getCreated());
        assertEquals(result.size(), 1);

        verify(requestDtoMapper, times(1)).toListOfDto(any());
        verify(requestRepository, times(1)).findAllByRequestorId(any());

    }

    @Test
    void getAllRequestsTest() {

        final long userId = 1L;


        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("desc")
                .creationTime(now())
                .build();

        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(requestRepository.findAllByRequestorIdOrderByCreationTimeDesc(anyLong()))
                .thenReturn(List.of(request));

        ItemRequestDto dto = ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreationTime()).build();

        when(requestDtoMapper.toListOfDto(any()))
                .thenReturn(List.of(dto));

        List<ItemRequestDto> result = requestService.getAllRequests(userId, null, null);

        assertEquals(result.get(0).getId(), dto.getId());
        assertEquals(result.get(0).getDescription(), dto.getDescription());
        assertEquals(result.get(0).getCreated(), dto.getCreated());

        verify(requestDtoMapper, times(1)).toListOfDto(any());
        verify(requestRepository, times(1)).findAllByRequestorIdOrderByCreationTimeDesc(anyLong());

    }

    @Test
    void getAllRequestsWithPaginationTest() {

        final long userId = 1L;
        final int from = 0;
        final int size = 10;

        ItemRequest itemRequest = ItemRequest.builder().id(1L)
                .description("desc")
                .creationTime(now()).build();
        List<ItemRequest> itemRequestList = List.of(itemRequest);
        Page<ItemRequest> page = new PageImpl<>(itemRequestList, PageRequest.of(from, size), itemRequestList.size());

        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(requestRepository.findAllByRequestor_IdNot(anyLong(), any()))
                .thenReturn(page);

        ItemRequestDto dto = ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreationTime()).build();

        when(requestDtoMapper.toListOfDto(any()))
                .thenReturn(List.of(dto));

        List<ItemRequestDto> result = requestService.getAllRequests(userId, from, size);

        assertEquals(result.get(0).getId(), dto.getId());
        assertEquals(result.get(0).getDescription(), dto.getDescription());
        assertEquals(result.get(0).getCreated(), dto.getCreated());

        verify(requestRepository, times(1)).findAllByRequestor_IdNot(anyLong(), any());
        verify(requestDtoMapper, times(1)).toListOfDto(any());
    }

    @Test
    void getRequestById() {

        when(requestRepository.existsById(anyLong()))
                .thenReturn(true);

        when(userRepository.existsById(anyLong())).thenReturn(true);

        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("desc")
                .creationTime(now()).build();
        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));

        ItemRequestDto dto = ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreationTime()).build();
        when(requestDtoMapper.toRequestDto(any()))
                .thenReturn(dto);

        ItemRequestDto result = requestService.getRequestById(1L, 1L);

        assertEquals(result.getId(), dto.getId());
        assertEquals(result.getDescription(), dto.getDescription());
        assertEquals(result.getCreated(), dto.getCreated());

        verify(requestRepository, times(1)).findById(anyLong());
        verify(requestDtoMapper, times(1)).toRequestDto(any());

    }

    @Test
    void requestServiceShouldThrowUserNotFoundException() {
        assertThrows(UserNotFoundException.class, () -> RequestService.checkUserExistsById(userRepository, 2L));
    }

    @Test
    void requestServiceShouldThrowRequestNotFoundException() {
        assertThrows(UserNotFoundException.class, () -> RequestService.checkRequestExistsById(requestRepository, 2L));
    }

}
