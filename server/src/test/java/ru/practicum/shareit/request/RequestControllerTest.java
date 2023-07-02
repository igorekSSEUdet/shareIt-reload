package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.utills.UserHttpHeaders.USER_ID;


@ExtendWith(MockitoExtension.class)
public class RequestControllerTest {

    private final ObjectMapper mapper = new ObjectMapper();
    @Mock
    private RequestService service;
    @InjectMocks
    private ItemRequestController controller;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void createRequest() throws Exception {

        final long userId = 1L;
        RequestDto requestDto = RequestDto.builder().description("desc").build();

        ItemRequestDto dto = ItemRequestDto.builder().id(1L).description("desc").build();

        when(service.createRequest(requestDto, userId))
                .thenReturn(dto);

        mvc.perform(post("/requests")
                        .header(USER_ID, userId)
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId().intValue())))
                .andExpect(jsonPath("$.description", is(dto.getDescription())));

    }

    @Test
    void getAllOwnRequestsTest() throws Exception {

        final long userId = 1L;

        ItemRequestDto dto = ItemRequestDto.builder().id(1L).description("desc").build();

        when(service.getAllOwnRequests(userId))
                .thenReturn(List.of(dto));

        mvc.perform(get("/requests")
                        .header(USER_ID, userId)
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is(dto.getId().intValue())))
                .andExpect(jsonPath("$.[0].description", is(dto.getDescription())));

    }

    @Test
    void getAllRequestsTest() throws Exception {

        final long userId = 1L;
        final Integer from = 0;
        final Integer size = 10;

        ItemRequestDto dto = ItemRequestDto.builder().id(1L).description("desc").build();

        when(service.getAllRequests(userId, from, size))
                .thenReturn(List.of(dto));

        mvc.perform(get("/requests/all")
                        .header(USER_ID, userId)
                        .param("from", from.toString())
                        .param("size", size.toString())
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].id", is(dto.getId().intValue())))
                .andExpect(jsonPath("$.[0].description", is(dto.getDescription())));

    }

    @Test
    void getRequestByIdTest() throws Exception {

        final long userId = 1L;
        final long requestId = 1L;

        ItemRequestDto dto = ItemRequestDto.builder().id(1L).description("desc").build();

        when(service.getRequestById(requestId, userId))
                .thenReturn(dto);

        mvc.perform(get("/requests/1")
                        .header(USER_ID, userId)
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId().intValue())))
                .andExpect(jsonPath("$.description", is(dto.getDescription())));

    }

}
