package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserPatchRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    @Test
    void getAll_shouldReturnUsers() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.ok().build();

        when(userClient.getAll()).thenReturn(response);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());

        verify(userClient, times(1)).getAll();
    }

    @Test
    void getUser_shouldReturnUser_whenExists() throws Exception {
        Long userId = 1L;
        ResponseEntity<Object> response = ResponseEntity.ok().build();

        when(userClient.getUser(userId)).thenReturn(response);

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userClient, times(1)).getUser(userId);
    }

    @Test
    void create_shouldReturnCreated_whenValidRequest() throws Exception {
        NewUserRequest request = new NewUserRequest();
        request.setName("Анна Смирнова");
        request.setEmail("anna.smirnova@mail.ru");

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.CREATED).build();

        when(userClient.createUser(any(NewUserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(userClient, times(1)).createUser(any(NewUserRequest.class));
    }

    @Test
    void create_shouldReturnBadRequest_whenNameIsNull() throws Exception {
        NewUserRequest request = new NewUserRequest();
        request.setEmail("anna.smirnova@mail.ru");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).createUser(any(NewUserRequest.class));
    }

    @Test
    void create_shouldReturnBadRequest_whenEmailIsNull() throws Exception {
        NewUserRequest request = new NewUserRequest();
        request.setName("Анна Смирнова");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).createUser(any(NewUserRequest.class));
    }

    @Test
    void create_shouldReturnBadRequest_whenEmailIsInvalid() throws Exception {
        NewUserRequest request = new NewUserRequest();
        request.setName("Анна Смирнова");
        request.setEmail("не-почта");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).createUser(any(NewUserRequest.class));
    }

    @Test
    void update_shouldReturnOk_whenValidRequest() throws Exception {
        Long userId = 1L;
        UpdateUserPatchRequest request = new UpdateUserPatchRequest();
        request.setName("Анна Иванова");
        request.setEmail("anna.ivanova@mail.ru");

        ResponseEntity<Object> response = ResponseEntity.ok().build();

        when(userClient.updateUser(eq(userId), any(UpdateUserPatchRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(userClient, times(1)).updateUser(eq(userId), any(UpdateUserPatchRequest.class));
    }

    @Test
    void update_shouldReturnBadRequest_whenEmailIsInvalid() throws Exception {
        Long userId = 1L;
        UpdateUserPatchRequest request = new UpdateUserPatchRequest();
        request.setEmail("не-почта");

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).updateUser(anyLong(), any(UpdateUserPatchRequest.class));
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        Long userId = 1L;

        doNothing().when(userClient).removeUser(userId);

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isNoContent());

        verify(userClient, times(1)).removeUser(userId);
    }
}
