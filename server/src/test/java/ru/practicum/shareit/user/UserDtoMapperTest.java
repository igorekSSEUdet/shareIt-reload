package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.ShortUserDto;
import ru.practicum.shareit.user.dto.UserCreationRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateRequestDto;
import ru.practicum.shareit.user.mapper.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserDtoMapperTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDtoMapper mapper;


    @Test
    public void toUserTest() {
        UserCreationRequestDto request = UserCreationRequestDto.builder()
                .name("name")
                .email("mail@mail.ru")
                .build();
        User result = mapper.toUser(request);
        assertEquals(result.getEmail(), request.getEmail());
        assertEquals(result.getName(), request.getName());

    }

    @Test
    public void toUserWithIdTest() {
        UserUpdateRequestDto request = UserUpdateRequestDto.builder()
                .name("name")
                .email("mail@mail.ru")
                .build();

        long userId = 1L;
        when(userRepository.getReferenceById(anyLong())).thenReturn(User.builder().id(1L).build());

        User result = mapper.toUser(request, userId);
        assertEquals(result.getEmail(), request.getEmail().get());
        assertEquals(result.getName(), request.getName().get());
    }

    @Test
    public void toUserDtoTest() {
        User user = User.builder().id(1L).name("name").email("mail@mail.ru").build();
        UserDto resultDto = mapper.toUserDto(user);
        assertEquals(resultDto.getEmail(), user.getEmail());
        assertEquals(resultDto.getName(), user.getName());
    }

    @Test
    public void toShortUserDto() {
        User user = User.builder().id(1L).name("name").email("mail@mail.ru").build();
        ShortUserDto dto = mapper.toShortUserDto(user);
        assertEquals(dto.getId(), user.getId());
    }

    @Test
    public void toUserDtoCollectionTest() {
        User user = User.builder().id(1L).name("name").email("mail@mail.ru").build();
        Collection<User> users = Collections.singletonList(user);
        List<UserDto> result = mapper.toUserDto(users);
        assertEquals(result.get(0).getId(), user.getId());
        assertEquals(result.get(0).getName(), user.getName());
        assertEquals(result.get(0).getEmail(), user.getEmail());


    }


}
