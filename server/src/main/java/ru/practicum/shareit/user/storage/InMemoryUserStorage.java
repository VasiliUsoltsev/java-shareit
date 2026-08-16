package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DuplicateDataException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserPatchRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users;

    private static final String DUBLICATE_EMAIL_EXCEPTION = "Пользователь с таким email уже существует";

    @Override
    public UserResponse createUser(NewUserRequest newUser) {
        User user = UserMapper.mapToUser(newUser);

        log.debug("Новый пользователь {}", user);

        if (isEmailDublicate(user.getEmail())) {
            throw new DuplicateDataException(DUBLICATE_EMAIL_EXCEPTION);
        }

        long userId = getNextId();

        user.setId(userId);

        users.put(userId, user);

        log.debug("Внесли пользователя {}", user);

        return UserMapper.mapToUserResponse(user);
    }

    @Override
    public UserResponse updateUser(Long userId, UpdateUserPatchRequest updateUserPatchRequest) {
        User updateUser = UserMapper.mapToUser(updateUserPatchRequest);

        User oldUser = users.get(userId);

        if (updateUser.getName() != null) {
            oldUser.setName(updateUser.getName());
        }

        if (updateUser.getEmail() != null) {
            String newEmail = updateUser.getEmail();
            String oldEmail = oldUser.getEmail();

            if (!newEmail.equals(oldEmail) && isEmailDublicate(updateUser.getEmail())) {
                throw new DuplicateDataException(DUBLICATE_EMAIL_EXCEPTION);
            }

            oldUser.setEmail(updateUser.getEmail());
        }

        return UserMapper.mapToUserResponse(oldUser);
    }

    @Override
    public void removeUser(Long removeUserId) {
        users.remove(removeUserId);
    }

    @Override
    public Collection<UserResponse> getAll() {
        return users.values()
                .stream()
                .map(UserMapper::mapToUserResponse)
                .toList();
    }

    @Override
    public UserResponse getUser(Long userId) {
        User user = getUserModel(userId);

        return UserMapper.mapToUserResponse(user);
    }

    @Override
    public User getUserModel(Long userId) {
        return users.get(userId);
    }

    @Override
    public boolean existsById(Long userId) {
        return users.containsKey(userId);
    }

    // Генерация идетификатора пользователя
    private Long getNextId() {
        Long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    // Проверка уникальности email пользователя
    private boolean isEmailDublicate(String email) {
        return users.values()
                .stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }
}
