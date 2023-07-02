package com.example.gateway.user.sevice;

import com.example.gateway.item.dto.UserCreationRequestDto;
import com.example.gateway.item.dto.UserUpdateRequestDto;
import org.springframework.http.ResponseEntity;

public interface UserRemoteCommand {

    ResponseEntity<Object> addUser(UserCreationRequestDto userDto);

    ResponseEntity<Object> updateUser(Long userId, UserUpdateRequestDto userDto);

    ResponseEntity<Object> getUserById(Long userId);

    ResponseEntity<Object> getAllUsers();

    ResponseEntity<Object> deleteUserById(Long userId);
}
