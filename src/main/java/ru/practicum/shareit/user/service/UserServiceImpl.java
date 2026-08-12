package ru.practicum.shareit.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserPatchRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public static final String USER_NOT_FOUND_EXCEPTION = "Пользователь с данным идентификатором не найден";

    @Override
    @Transactional
    public UserResponse createUser(NewUserRequest newUser) {
        User user = userRepository.save(UserMapper.mapToUser(newUser));

        return UserMapper.mapToUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long userId, UpdateUserPatchRequest updateUserPatchRequest) {
        User oldUser = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(USER_NOT_FOUND_EXCEPTION));

        User updateUser = UserMapper.mapToUser(updateUserPatchRequest);

        if (updateUser.getName() != null) {
            oldUser.setName(updateUser.getName());
        }

        if (updateUser.getEmail() != null) {
            oldUser.setEmail(updateUser.getEmail());
        }

        userRepository.save(oldUser);

        return UserMapper.mapToUserResponse(oldUser);
    }

    @Override
    @Transactional
    public void removeUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_EXCEPTION));

        userRepository.delete(user);
    }

    @Override
    public Collection<UserResponse> getAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::mapToUserResponse)
                .toList();
    }

    @Override
    public UserResponse getUser(Long userId) {
        return UserMapper.mapToUserResponse(getUserModel(userId));
    }

    @Override
    public User getUserModel(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_EXCEPTION));
    }

    @Override
    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }
}
