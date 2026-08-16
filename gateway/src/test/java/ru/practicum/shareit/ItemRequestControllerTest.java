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
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.NewItemRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestClient itemRequestClient;

    @Test
    void getAllItemRequestsRequester_shouldReturnRequests() throws Exception {
        Long userId = 1L;
        ResponseEntity<Object> response = ResponseEntity.ok().build();

        when(itemRequestClient.getAllItemRequestsRequester(userId)).thenReturn(response);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemRequestClient, times(1)).getAllItemRequestsRequester(userId);
    }

    @Test
    void getAllItemRequestsRequester_shouldReturnBadRequest_whenUserIdMissing() throws Exception {
        mockMvc.perform(get("/requests"))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).getAllItemRequestsRequester(anyLong());
    }

    @Test
    void getAll_shouldReturnAllRequests() throws Exception {
        Long userId = 1L;
        ResponseEntity<Object> response = ResponseEntity.ok().build();

        when(itemRequestClient.getAll(userId)).thenReturn(response);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemRequestClient, times(1)).getAll(userId);
    }

    @Test
    void getAll_shouldReturnBadRequest_whenUserIdMissing() throws Exception {
        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).getAll(anyLong());
    }

    @Test
    void getItemRequest_shouldReturnRequest_whenExists() throws Exception {
        Long requestId = 1L;
        ResponseEntity<Object> response = ResponseEntity.ok().build();

        when(itemRequestClient.getItemRequest(requestId)).thenReturn(response);

        mockMvc.perform(get("/requests/{requestId}", requestId))
                .andExpect(status().isOk());

        verify(itemRequestClient, times(1)).getItemRequest(requestId);
    }

    @Test
    void createItemRequest_shouldReturnCreated_whenValidRequest() throws Exception {
        Long userId = 1L;
        NewItemRequest request = new NewItemRequest();
        request.setDescription("Нужна аккумуляторная дрель");

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.CREATED).build();

        when(itemRequestClient.createItemRequest(any(NewItemRequest.class), eq(userId))).thenReturn(response);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(itemRequestClient, times(1)).createItemRequest(any(NewItemRequest.class), eq(userId));
    }

    @Test
    void createItemRequest_shouldReturnBadRequest_whenDescriptionIsEmpty() throws Exception {
        Long userId = 1L;
        NewItemRequest request = new NewItemRequest();
        request.setDescription("");

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).createItemRequest(any(NewItemRequest.class), anyLong());
    }

    @Test
    void createItemRequest_shouldReturnBadRequest_whenDescriptionIsNull() throws Exception {
        Long userId = 1L;
        NewItemRequest request = new NewItemRequest();

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).createItemRequest(any(NewItemRequest.class), anyLong());
    }

    @Test
    void createItemRequest_shouldReturnBadRequest_whenUserIdMissing() throws Exception {
        NewItemRequest request = new NewItemRequest();
        request.setDescription("Нужна аккумуляторная дрель");

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).createItemRequest(any(NewItemRequest.class), anyLong());
    }
}
