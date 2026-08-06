package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserPatchRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserStorage {
    public UserDto createUser(NewUserRequest newUser);

    public UserDto updateUser(Integer userId, UpdateUserPatchRequest updateUserPatchRequest);

    public void removeUser(Integer removeUserId);

    public Collection<UserDto> getAll();

    public UserDto getUser(Integer id);

}
