package ru.practicum.shareit.item;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.comment.CommentService;

import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.utills.UserHttpHeaders.USER_ID;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

    private final ObjectMapper mapper = new ObjectMapper();
    @Mock
    private ItemService itemService;
    @Mock
    private CommentService commentService;
    private MockMvc mvc;

    @InjectMocks
    private ItemController controller;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void addItemTest() throws Exception {

        final long ownerId = 1L;
        ItemCreationRequestDto requestDto = ItemCreationRequestDto.builder()
                .available(true)
                .description("desc")
                .name("name").build();

        ItemDto dto = ItemDto.builder()
                .id(1L)
                .available(true)
                .description("desc")
                .name("name").build();

        when(itemService.addItem(requestDto, ownerId))
                .thenReturn(dto);

        mvc.perform(post("/items")
                        .header(USER_ID, ownerId)
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.available", equalTo(dto.getAvailable())));

    }

    @Test
    void updateItemTest() throws Exception {

        final long itemId = 1L;
        final long userId = 1L;
        ItemUpdateRequestDto requestDto = ItemUpdateRequestDto.builder()
                .available(true)
                .description("desc")
                .name("name").build();

        ItemDto dto = ItemDto.builder()
                .id(1L)
                .available(true)
                .description("desc")
                .name("name").build();

        when(itemService.updateItem(requestDto, itemId, userId))
                .thenReturn(dto);

        mvc.perform(patch("/items/1")
                        .header(USER_ID, itemId)
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.available", equalTo(dto.getAvailable())));

    }

    @Test
    void getItemByIdTest() throws Exception {

        final long itemId = 1L;
        final long userId = 1L;

        DetailedItemDto detailedDto = DetailedItemDto.builder()
                .id(1L)
                .available(true)
                .description("desc")
                .name("name").build();

        when(itemService.getItemByItemId(userId, itemId))
                .thenReturn(detailedDto);

        mvc.perform(get("/items/1")
                        .header(USER_ID, itemId)
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(detailedDto.getId()), Long.class))
                .andExpect(jsonPath("$.available", equalTo(detailedDto.getAvailable())));

    }

    @Test
    void getItemsByOwnerIdTest() throws Exception {

        final long ownerId = 1L;

        DetailedItemDto detailedDto = DetailedItemDto.builder()
                .id(1L)
                .available(true)
                .description("desc")
                .name("name").build();

        when(itemService.getItemsByOwnerId(ownerId, null, null))
                .thenReturn(List.of(detailedDto));

        mvc.perform(get("/items")
                        .header(USER_ID, ownerId)
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].available", equalTo(detailedDto.getAvailable())))
                .andExpect(jsonPath("$[0].id", is(detailedDto.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(detailedDto.getName())))
                .andExpect(jsonPath("$[0].description", is(detailedDto.getDescription())));

    }

    @Test
    void searchItemsByNameOrDescriptionTest() throws Exception {

        final long ownerId = 1L;
        final String searchText = "searchText";
        ItemDto detailedDto = ItemDto.builder()
                .id(1L)
                .available(true)
                .description("desc")
                .name("name").build();

        when(itemService.searchItemsByNameOrDescription(searchText, null, null))
                .thenReturn(List.of(detailedDto));

        mvc.perform(get("/items/search")
                        .param("text", searchText)
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].available", equalTo(detailedDto.getAvailable())))
                .andExpect(jsonPath("$[0].id", is(detailedDto.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(detailedDto.getName())))
                .andExpect(jsonPath("$[0].description", is(detailedDto.getDescription())));

    }

    @Test
    void addCommentTest() throws Exception {

        final long userId = 1L;
        final long itemId = 1L;

        RequestCommentDto comment = RequestCommentDto.builder().text("comment").build();

        CommentDto commentDto = CommentDto.builder().id(1L).text("comment").authorName("name").build();

        when(commentService.addComment(comment, userId, itemId))
                .thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .header(USER_ID, userId)
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId().intValue())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.text", is(commentDto.getText())));


    }

}
