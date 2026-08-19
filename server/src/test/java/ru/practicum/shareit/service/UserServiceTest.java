package ru.practicum.shareit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserPatchRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_shouldReturnUserResponse_whenValidRequest() {
        NewUserRequest request = new NewUserRequest();
        request.setName("Анна Смирнова");
        request.setEmail("anna@mail.ru");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("Анна Смирнова");
        savedUser.setEmail("anna@mail.ru");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = userService.createUser(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Анна Смирнова");
        assertThat(response.getEmail()).isEqualTo("anna@mail.ru");

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_shouldMapFieldsCorrectly() {
        NewUserRequest request = new NewUserRequest();
        request.setName("Иван Петров");
        request.setEmail("ivan@mail.ru");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        UserResponse response = userService.createUser(request);

        assertThat(response.getName()).isEqualTo("Иван Петров");
        assertThat(response.getEmail()).isEqualTo("ivan@mail.ru");
    }

    @Test
    void updateUser_shouldUpdateNameAndEmail_whenBothProvided() {
        Long userId = 1L;
        User oldUser = new User();
        oldUser.setId(userId);
        oldUser.setName("Анна Смирнова");
        oldUser.setEmail("anna@mail.ru");

        UpdateUserPatchRequest request = new UpdateUserPatchRequest();
        request.setName("Анна Иванова");
        request.setEmail("anna.ivanova@mail.ru");

        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));
        when(userRepository.save(any(User.class))).thenReturn(oldUser);

        UserResponse response = userService.updateUser(userId, request);

        assertThat(response.getName()).isEqualTo("Анна Иванова");
        assertThat(response.getEmail()).isEqualTo("anna.ivanova@mail.ru");

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(oldUser);
    }

    @Test
    void updateUser_shouldUpdateOnlyName_whenEmailIsNull() {
        Long userId = 1L;
        User oldUser = new User();
        oldUser.setId(userId);
        oldUser.setName("Анна Смирнова");
        oldUser.setEmail("anna@mail.ru");

        UpdateUserPatchRequest request = new UpdateUserPatchRequest();
        request.setName("Анна Иванова");

        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));
        when(userRepository.save(any(User.class))).thenReturn(oldUser);

        UserResponse response = userService.updateUser(userId, request);

        assertThat(response.getName()).isEqualTo("Анна Иванова");
        assertThat(response.getEmail()).isEqualTo("anna@mail.ru");

        verify(userRepository, times(1)).save(oldUser);
    }

    @Test
    void updateUser_shouldUpdateOnlyEmail_whenNameIsNull() {
        Long userId = 1L;
        User oldUser = new User();
        oldUser.setId(userId);
        oldUser.setName("Анна Смирнова");
        oldUser.setEmail("anna@mail.ru");

        UpdateUserPatchRequest request = new UpdateUserPatchRequest();
        request.setEmail("anna.new@mail.ru");

        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));
        when(userRepository.save(any(User.class))).thenReturn(oldUser);

        UserResponse response = userService.updateUser(userId, request);

        assertThat(response.getName()).isEqualTo("Анна Смирнова");
        assertThat(response.getEmail()).isEqualTo("anna.new@mail.ru");

        verify(userRepository, times(1)).save(oldUser);
    }

    @Test
    void updateUser_shouldThrowNotFoundException_whenUserNotFound() {
        Long userId = 99L;
        UpdateUserPatchRequest request = new UpdateUserPatchRequest();
        request.setName("Несуществующий");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(userId, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь с данным идентификатором не найден");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUser_shouldReturnUserResponse_whenUserExists() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setName("Анна Смирнова");
        user.setEmail("anna@mail.ru");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserResponse response = userService.getUser(userId);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(userId);
        assertThat(response.getName()).isEqualTo("Анна Смирнова");
        assertThat(response.getEmail()).isEqualTo("anna@mail.ru");

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUser_shouldThrowNotFoundException_whenUserNotFound() {
        Long userId = 99L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUser(userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь с данным идентификатором не найден");

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserModel_shouldReturnUser_whenUserExists() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setName("Анна Смирнова");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.getUserModel(userId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getName()).isEqualTo("Анна Смирнова");

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserModel_shouldThrowNotFoundException_whenUserNotFound() {
        Long userId = 99L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserModel(userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь с данным идентификатором не найден");
    }

    @Test
    void getAll_shouldReturnAllUsers() {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("Анна Смирнова");
        user1.setEmail("anna@mail.ru");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Иван Петров");
        user2.setEmail("ivan@mail.ru");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        Collection<UserResponse> responses = userService.getAll();

        assertThat(responses).hasSize(2);
        assertThat(responses).extracting(UserResponse::getName)
                .containsExactlyInAnyOrder("Анна Смирнова", "Иван Петров");

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getAll_shouldReturnEmptyList_whenNoUsers() {
        when(userRepository.findAll()).thenReturn(List.of());

        Collection<UserResponse> responses = userService.getAll();

        assertThat(responses).isEmpty();
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void existsById_shouldReturnTrue_whenUserExists() {
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);

        boolean result = userService.existsById(userId);

        assertThat(result).isTrue();
        verify(userRepository, times(1)).existsById(userId);
    }

    @Test
    void existsById_shouldReturnFalse_whenUserNotFound() {
        Long userId = 99L;

        when(userRepository.existsById(userId)).thenReturn(false);

        boolean result = userService.existsById(userId);

        assertThat(result).isFalse();
        verify(userRepository, times(1)).existsById(userId);
    }

    @Test
    void removeUser_shouldDeleteUser_whenUserExists() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setName("Анна Смирнова");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.removeUser(userId);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void removeUser_shouldThrowNotFoundException_whenUserNotFound() {
        Long userId = 99L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.removeUser(userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь с данным идентификатором не найден");

        verify(userRepository, never()).delete(any(User.class));
    }
}
