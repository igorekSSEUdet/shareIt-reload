package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserCreationRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserDtoMapper userDtoMapper;
    private UserService userService;

    @BeforeEach
    public void setUp() {
        userService = new UserServiceImpl(userRepository, userDtoMapper);
    }


    @Test
    public void addUserTest() {

        when(userRepository.save(any()))
                .thenReturn(new User(1L, "some name", "mail@mail.ru"));


        when(userDtoMapper.toUserDto((User) any()))
                .thenReturn(UserDto.builder().id(1L).name("some name").email("mail@mail.ru").build());

        UserDto testUser = userService.addUser(UserCreationRequestDto.builder().name("some name").email("mail@mail.ru").build());
        assertEquals(testUser, UserDto.builder().id(1L).name("some name").email("mail@mail.ru").build());
    }

    @Test
    public void updateUserTest() {

        User userForUpdate = User.builder().name("update name").email("update@mail.ru").build();

        when(userRepository.save(any()))
                .thenReturn(userForUpdate);

        User updatedUser = userRepository.save(userForUpdate);

        assertEquals(updatedUser, userForUpdate);

    }

    @Test
    public void getUserByIdTest() {

        User user = new User();
        user.setId(1L);
        user.setName("testUser");
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.getReferenceById(anyLong())).thenReturn(user);

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());

        when(userDtoMapper.toUserDto(user)).thenReturn(userDto);
        Long userId = 1L;
        UserDto result = userService.getUserById(userId);

        assertEquals(userDto, result);
    }

    @Test
    public void getAllUsersTest() {
        User user1 = new User();
        User user2 = new User();
        List<User> users = Arrays.asList(user1, user2);

        UserDto userDto1 = new UserDto();
        UserDto userDto2 = new UserDto();
        List<UserDto> userDtos = Arrays.asList(userDto1, userDto2);

        given(userRepository.findAll()).willReturn(users);
        given(userDtoMapper.toUserDto(users)).willReturn(userDtos);

        List<UserDto> result = userService.getAllUsers();

        assertEquals(result.size(), 2);
        verify(userRepository, times(1)).findAll();
        verify(userDtoMapper, times(1)).toUserDto(users);
    }

    @Test
    public void deleteUserByIdTest() {
        Long userId = 1L;
        when(userRepository.existsById(anyLong())).thenReturn(true);

        userService.deleteUserById(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }


}
