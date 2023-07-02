package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.dto.UserCreationRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateRequestDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserServiceImplIntegrationTest {

    private final UserCreationRequestDto userRequest = UserCreationRequestDto.builder().name("name").email("mail@mail.ru").build();
    @Autowired
    private UserServiceImpl userService;

    public UserServiceImplIntegrationTest() {
    }

    @Test
    @DirtiesContext
    public void addUserTest() {
        UserDto dto = userService.addUser(userRequest);
        assertEquals(dto.getName(), userRequest.getName());
        assertEquals(dto.getEmail(), userRequest.getEmail());
        assertEquals(dto.getId(), 1L);

    }

    @Test
    @DirtiesContext
    public void updateUserTest() {
        userService.addUser(userRequest);
        UserUpdateRequestDto updateRequestDto = UserUpdateRequestDto.builder().name("update").email("update@google.com").build();
        UserDto updatedUser = userService.updateUser(updateRequestDto, 1L);
        assertEquals(updatedUser.getName(), updateRequestDto.getName().orElseThrow());
        assertEquals(updatedUser.getEmail(), updateRequestDto.getEmail().orElseThrow());
        assertEquals(updatedUser.getId(), 1L);
    }

    @Test
    @DirtiesContext
    public void getUserByIdTest() {
        UserDto dto = userService.addUser(userRequest);
        UserDto user = userService.getUserById(1L);
        assertEquals(user.getName(), dto.getName());
        assertEquals(user.getEmail(), dto.getEmail());
        assertEquals(user.getId(), 1L);
    }

    @Test
    @DirtiesContext
    public void getAllUsersTest() {
        UserDto dto1 = userService.addUser(userRequest);

        List<UserDto> result = userService.getAllUsers();

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getName(), dto1.getName());
        assertEquals(result.get(0).getEmail(), dto1.getEmail());

    }

    @Test
    @DirtiesContext
    public void deleteUserByIdTest() {
        userService.addUser(userRequest);
        userService.deleteUserById(1L);

        List<UserDto> result = userService.getAllUsers();

        assertEquals(result.size(), 0);

    }
}
