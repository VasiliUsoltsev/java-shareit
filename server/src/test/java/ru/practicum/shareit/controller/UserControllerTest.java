package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserPatchRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void getAll_shouldReturnUsers() throws Exception {
        UserResponse response1 = new UserResponse();
        response1.setId(1L);
        response1.setName("Анна Смирнова");
        response1.setEmail("anna@mail.ru");

        UserResponse response2 = new UserResponse();
        response2.setId(2L);
        response2.setName("Иван Петров");
        response2.setEmail("ivan@mail.ru");

        when(userService.getAll()).thenReturn(List.of(response1, response2));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Анна Смирнова"))
                .andExpect(jsonPath("$[0].email").value("anna@mail.ru"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Иван Петров"))
                .andExpect(jsonPath("$[1].email").value("ivan@mail.ru"));

        verify(userService, times(1)).getAll();
    }

    @Test
    void getAll_shouldReturnEmptyList() throws Exception {
        when(userService.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(userService, times(1)).getAll();
    }

    @Test
    void getUser_shouldReturnUser() throws Exception {
        Long userId = 1L;

        UserResponse response = new UserResponse();
        response.setId(userId);
        response.setName("Анна Смирнова");
        response.setEmail("anna@mail.ru");

        when(userService.getUser(userId)).thenReturn(response);

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Анна Смирнова"))
                .andExpect(jsonPath("$.email").value("anna@mail.ru"));

        verify(userService, times(1)).getUser(userId);
    }

    @Test
    void getUser_shouldReturnBadRequest_whenUserIdIsInvalid() throws Exception {
        mockMvc.perform(get("/users/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_shouldReturnCreated() throws Exception {
        NewUserRequest request = new NewUserRequest();
        request.setName("Анна Смирнова");
        request.setEmail("anna@mail.ru");

        UserResponse response = new UserResponse();
        response.setId(1L);
        response.setName("Анна Смирнова");
        response.setEmail("anna@mail.ru");

        when(userService.createUser(any(NewUserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Анна Смирнова"))
                .andExpect(jsonPath("$.email").value("anna@mail.ru"));

        verify(userService, times(1)).createUser(any(NewUserRequest.class));
    }

    @Test
    void update_shouldReturnOk() throws Exception {
        Long userId = 1L;

        UpdateUserPatchRequest request = new UpdateUserPatchRequest();
        request.setName("Анна Иванова");
        request.setEmail("anna.ivanova@mail.ru");

        UserResponse response = new UserResponse();
        response.setId(userId);
        response.setName("Анна Иванова");
        response.setEmail("anna.ivanova@mail.ru");

        when(userService.updateUser(eq(userId), any(UpdateUserPatchRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Анна Иванова"))
                .andExpect(jsonPath("$.email").value("anna.ivanova@mail.ru"));

        verify(userService, times(1)).updateUser(eq(userId), any(UpdateUserPatchRequest.class));
    }

    @Test
    void update_shouldReturnOk_whenOnlyNameProvided() throws Exception {
        Long userId = 1L;

        UpdateUserPatchRequest request = new UpdateUserPatchRequest();
        request.setName("Анна Иванова");

        UserResponse response = new UserResponse();
        response.setId(userId);
        response.setName("Анна Иванова");
        response.setEmail("anna@mail.ru");

        when(userService.updateUser(eq(userId), any(UpdateUserPatchRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Анна Иванова"))
                .andExpect(jsonPath("$.email").value("anna@mail.ru"));

        verify(userService, times(1)).updateUser(eq(userId), any(UpdateUserPatchRequest.class));
    }

    @Test
    void update_shouldReturnOk_whenOnlyEmailProvided() throws Exception {
        Long userId = 1L;

        UpdateUserPatchRequest request = new UpdateUserPatchRequest();
        request.setEmail("anna.ivanova@mail.ru");

        UserResponse response = new UserResponse();
        response.setId(userId);
        response.setName("Анна Смирнова");
        response.setEmail("anna.ivanova@mail.ru");

        when(userService.updateUser(eq(userId), any(UpdateUserPatchRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Анна Смирнова"))
                .andExpect(jsonPath("$.email").value("anna.ivanova@mail.ru"));

        verify(userService, times(1)).updateUser(eq(userId), any(UpdateUserPatchRequest.class));
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        Long userId = 1L;

        doNothing().when(userService).removeUser(userId);

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).removeUser(userId);
    }

    @Test
    void delete_shouldReturnBadRequest_whenUserIdIsInvalid() throws Exception {
        mockMvc.perform(delete("/users/abc"))
                .andExpect(status().isBadRequest());
    }
}