package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserPatchRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {
    public UserResponse createUser(NewUserRequest newUser);

    public UserResponse updateUser(Long userId, UpdateUserPatchRequest updateUserPatchRequest);

    public void removeUser(Long removeUserId);

    public Collection<UserResponse> getAll();

    public UserResponse getUser(Long userId);

    public User getUserModel(Long userId);

    public boolean existsById(Long userId);
}
