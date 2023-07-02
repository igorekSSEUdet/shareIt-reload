package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserCreationRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateRequestDto;
import ru.practicum.shareit.user.mapper.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static ru.practicum.shareit.user.service.UserService.checkUserExistsById;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserDtoMapper userDtoMapper;


    @Override
    public UserDto addUser(UserCreationRequestDto userDto) {
        User user = userDtoMapper.toUser(userDto);
        User addedUser = userRepository.save(user);
        log.debug("User ID_{} added.", addedUser.getId());
        return userDtoMapper.toUserDto(addedUser);
    }

    @Override
    public UserDto updateUser(UserUpdateRequestDto userDto, Long userId) {
        checkUserExistsById(userRepository, userId);
        User user = userDtoMapper.toUser(userDto, userId);
        User updatedUser = userRepository.save(user);
        log.debug("User ID_{} updated.", updatedUser.getId());
        return userDtoMapper.toUserDto(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long userId) {
        checkUserExistsById(userRepository, userId);
        User user = userRepository.getReferenceById(userId);
        log.debug("User ID_{} returned.", user.getId());
        return userDtoMapper.toUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        log.debug("All users returned, {} in total.", users.size());
        return userDtoMapper.toUserDto(users);
    }

    @Override
    public void deleteUserById(Long userId) {
        checkUserExistsById(userRepository, userId);
        log.debug("User ID_{} deleted.", userId);
        userRepository.deleteById(userId);
    }
}
