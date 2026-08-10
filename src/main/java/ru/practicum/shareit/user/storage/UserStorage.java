package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserPatchRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {
    public UserResponse createUser(NewUserRequest newUser);

    public UserResponse updateUser(Integer userId, UpdateUserPatchRequest updateUserPatchRequest);

    public void removeUser(Integer removeUserId);

    public Collection<UserResponse> getAll();

    public UserResponse getUser(Integer userId);

    public User getUserModel(Integer userId);

    public boolean existsById(Integer userId);
}
