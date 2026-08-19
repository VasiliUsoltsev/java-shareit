package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserPatchRequest;
import ru.practicum.shareit.user.dto.UserBookingResponse;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    @Test
    void mapToUserResponse_shouldMapUser() {
        User user = new User();
        user.setId(1L);
        user.setName("Анна Смирнова");
        user.setEmail("anna.smirnova@mail.ru");

        UserResponse response = UserMapper.mapToUserResponse(user);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Анна Смирнова");
        assertThat(response.getEmail()).isEqualTo("anna.smirnova@mail.ru");
    }

    @Test
    void mapToUserResponse_shouldHandleNullUser() {
        UserResponse response = UserMapper.mapToUserResponse(null);

        assertThat(response).isNull();
    }

    @Test
    void mapToUserResponse_shouldHandleUserWithoutEmail() {
        User user = new User();
        user.setId(2L);
        user.setName("Иван Петров");

        UserResponse response = UserMapper.mapToUserResponse(user);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(2L);
        assertThat(response.getName()).isEqualTo("Иван Петров");
        assertThat(response.getEmail()).isNull();
    }

    @Test
    void mapToUser_fromNewUserRequest_shouldMapAllFields() {
        NewUserRequest request = new NewUserRequest();
        request.setName("Анна Смирнова");
        request.setEmail("anna.smirnova@mail.ru");

        User user = UserMapper.mapToUser(request);

        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo("Анна Смирнова");
        assertThat(user.getEmail()).isEqualTo("anna.smirnova@mail.ru");
    }

    @Test
    void mapToUser_fromNewUserRequest_shouldHandleNullFields() {
        NewUserRequest request = new NewUserRequest();

        User user = UserMapper.mapToUser(request);

        assertThat(user).isNotNull();
        assertThat(user.getName()).isNull();
        assertThat(user.getEmail()).isNull();
    }

    @Test
    void mapToUser_fromNewUserRequest_shouldHandleNullRequest() {
        User user = UserMapper.mapToUser((NewUserRequest) null);

        assertThat(user).isNull();
    }

    @Test
    void mapToUser_fromUpdateUserPatchRequest_shouldMapAllFields() {
        UpdateUserPatchRequest request = new UpdateUserPatchRequest();
        request.setName("Анна Иванова");
        request.setEmail("anna.ivanova@mail.ru");

        User user = UserMapper.mapToUser(request);

        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo("Анна Иванова");
        assertThat(user.getEmail()).isEqualTo("anna.ivanova@mail.ru");
    }

    @Test
    void mapToUser_fromUpdateUserPatchRequest_shouldHandleNullFields() {
        UpdateUserPatchRequest request = new UpdateUserPatchRequest();

        User user = UserMapper.mapToUser(request);

        assertThat(user).isNotNull();
        assertThat(user.getName()).isNull();
        assertThat(user.getEmail()).isNull();
    }

    @Test
    void mapToUser_fromUpdateUserPatchRequest_shouldHandleNullRequest() {
        User user = UserMapper.mapToUser((UpdateUserPatchRequest) null);

        assertThat(user).isNull();
    }

    @Test
    void mapToUserBookingResponse_shouldMapUser() {
        User user = new User();
        user.setId(5L);
        user.setName("Анна Смирнова");
        user.setEmail("anna@mail.ru");

        UserBookingResponse response = UserMapper.mapToUserBookingResponse(user);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(5L);
    }

    @Test
    void mapToUserBookingResponse_shouldHandleNullUser() {
        UserBookingResponse response = UserMapper.mapToUserBookingResponse(null);

        assertThat(response).isNull();
    }

    @Test
    void mapToUserBookingResponse_shouldMapOnlyId() {
        User user = new User();
        user.setId(10L);

        UserBookingResponse response = UserMapper.mapToUserBookingResponse(user);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(10L);
    }
}