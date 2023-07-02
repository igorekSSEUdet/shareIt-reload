package ru.practicum.shareit.user.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.ShortUserDto;
import ru.practicum.shareit.user.dto.UserCreationRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserDtoMapper {
    private final UserRepository userRepository;

    public User toUser(UserCreationRequestDto userDto) {
        User user = new User();

        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());

        return user;
    }

    public User toUser(UserUpdateRequestDto userDto, Long userId) {
        User user = userRepository.getReferenceById(userId);

        userDto.getName().ifPresent(user::setName);
        userDto.getEmail().ifPresent(user::setEmail);

        return user;
    }

    public UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();

        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());

        return userDto;
    }

    public ShortUserDto toShortUserDto(User user) {
        ShortUserDto userDto = new ShortUserDto();

        userDto.setId(user.getId());

        return userDto;
    }

    public List<UserDto> toUserDto(Collection<User> users) {
        return users.stream()
                .map(this::toUserDto)
                .collect(Collectors.toList());
    }
}
