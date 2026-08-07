package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserPatchRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {
    public UserDto createUser(NewUserRequest newUser);

    public UserDto updateUser(Integer userId, UpdateUserPatchRequest updateUserPatchRequest);

    public void removeUser(Integer removeUserId);

    public Collection<UserDto> getAll();

    public UserDto getUser(Integer userId);

    public User getUserModel(Integer userId);

    public boolean existsById(Integer userId);
}
