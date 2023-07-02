package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.RequestCommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.comment.Comment;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.CommentDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentDtoMapperTest {

    @InjectMocks
    private CommentDtoMapper mapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @Test
    public void toCommentTest() {

        RequestCommentDto request = RequestCommentDto.builder().text("comment").build();
        long authorId = 1L;
        long itemId = 1L;

        User author = User.builder().id(1L).email("mail@mail.ru").name("name").build();
        Item item = Item.builder().id(1L).name("name").available(Boolean.TRUE).description("desc").build();

        when(userRepository.getReferenceById(anyLong())).thenReturn(author);
        when(itemRepository.getReferenceById(anyLong())).thenReturn(item);

        Comment result = mapper.toComment(request, authorId, itemId);
        assertEquals(result.getText(), request.getText());
        assertEquals(result.getAuthor(), author);
        assertEquals(result.getItem(), item);


    }

    @Test
    public void toCommentDtoTest() {
        User author = User.builder().id(1L).email("mail@mail.ru").name("name").build();
        Comment request = Comment.builder().id(1L).created(now()).text("comment").author(author).build();
        CommentDto resultDto = mapper.toCommentDto(request);
        assertEquals(resultDto.getText(), request.getText());
        assertEquals(resultDto.getId(), request.getId());
        assertEquals(resultDto.getAuthorName(), author.getName());
    }

    @Test
    public void toCommentDtoCollectionTest() {
        User author = User.builder().id(1L).email("mail@mail.ru").name("name").build();
        Comment request = Comment.builder().id(1L).created(now()).text("comment").author(author).build();
        Collection<Comment> comments = Collections.singletonList(request);
        List<CommentDto> result = mapper.toCommentDto(comments);
        assertEquals(result.get(0).getId(), request.getId());
        assertEquals(result.get(0).getText(), request.getText());
        assertEquals(result.get(0).getAuthorName(), request.getAuthor().getName());


    }

}
