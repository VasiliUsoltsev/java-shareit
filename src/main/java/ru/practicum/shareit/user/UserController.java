package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserPatchRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserStorage userStorage;

    @GetMapping
    public Collection<UserResponse> getAll() {
        return userStorage.getAll();
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getUser(@PathVariable Integer userId) {
        return userStorage.getUser(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody NewUserRequest newUser) {
        return userStorage.createUser(newUser);
    }

    @PatchMapping("/{userId}")
    public UserResponse update(@Valid @RequestBody UpdateUserPatchRequest updateUser,
                               @PathVariable Integer userId
    ) {
        return userStorage.updateUser(userId, updateUser);
    }

    @DeleteMapping("/{removeUserId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable() Integer removeUserId) {
        userStorage.removeUser(removeUserId);
    }
}
