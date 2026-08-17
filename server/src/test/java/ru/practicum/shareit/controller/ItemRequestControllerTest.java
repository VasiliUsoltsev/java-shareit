package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestBaseResponse;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersResponse;
import ru.practicum.shareit.request.dto.NewItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void getAllItemRequestsRequester_shouldReturnRequests() throws Exception {
        Long userId = 1L;

        ItemRequestWithAnswersResponse response1 = new ItemRequestWithAnswersResponse();
        response1.setId(1L);
        response1.setDescription("Нужна дрель");
        response1.setCreated(LocalDateTime.now());

        ItemRequestWithAnswersResponse response2 = new ItemRequestWithAnswersResponse();
        response2.setId(2L);
        response2.setDescription("Нужна отвертка");
        response2.setCreated(LocalDateTime.now());

        when(itemRequestService.getAllItemRequestsRequester(userId)).thenReturn(List.of(response1, response2));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Нужна дрель"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].description").value("Нужна отвертка"));

        verify(itemRequestService, times(1)).getAllItemRequestsRequester(userId);
    }

    @Test
    void getAllItemRequestsRequester_shouldReturnBadRequest_whenUserIdMissing() throws Exception {
        mockMvc.perform(get("/requests"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAll_shouldReturnAllRequests() throws Exception {
        Long userId = 1L;

        ItemRequestBaseResponse response1 = new ItemRequestBaseResponse();
        response1.setId(1L);
        response1.setDescription("Нужна дрель");
        response1.setCreated(LocalDateTime.now());

        ItemRequestBaseResponse response2 = new ItemRequestBaseResponse();
        response2.setId(2L);
        response2.setDescription("Нужна отвертка");
        response2.setCreated(LocalDateTime.now());

        when(itemRequestService.getAll(userId)).thenReturn(List.of(response1, response2));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Нужна дрель"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].description").value("Нужна отвертка"));

        verify(itemRequestService, times(1)).getAll(userId);
    }

    @Test
    void getAll_shouldReturnBadRequest_whenUserIdMissing() throws Exception {
        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getItemRequest_shouldReturnRequest() throws Exception {
        Long requestId = 1L;

        ItemRequestWithAnswersResponse response = new ItemRequestWithAnswersResponse();
        response.setId(requestId);
        response.setDescription("Нужна аккумуляторная дрель");
        response.setCreated(LocalDateTime.now());

        when(itemRequestService.getItemRequest(requestId)).thenReturn(response);

        mockMvc.perform(get("/requests/{requestId}", requestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Нужна аккумуляторная дрель"));

        verify(itemRequestService, times(1)).getItemRequest(requestId);
    }

    @Test
    void getItemRequest_shouldReturnRequestWithAnswers() throws Exception {
        Long requestId = 1L;

        ItemRequestWithAnswersResponse response = new ItemRequestWithAnswersResponse();
        response.setId(requestId);
        response.setDescription("Нужна дрель");
        response.setCreated(LocalDateTime.now());
        response.setItems(List.of());

        when(itemRequestService.getItemRequest(requestId)).thenReturn(response);

        mockMvc.perform(get("/requests/{requestId}", requestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.items").exists());

        verify(itemRequestService, times(1)).getItemRequest(requestId);
    }

    @Test
    void createItemRequest_shouldReturnCreated() throws Exception {
        Long userId = 1L;

        NewItemRequest request = new NewItemRequest();
        request.setDescription("Нужна аккумуляторная дрель");

        ItemRequestBaseResponse response = new ItemRequestBaseResponse();
        response.setId(1L);
        response.setDescription("Нужна аккумуляторная дрель");
        response.setCreated(LocalDateTime.now());

        when(itemRequestService.createItemRequest(any(NewItemRequest.class), eq(userId))).thenReturn(response);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Нужна аккумуляторная дрель"));

        verify(itemRequestService, times(1)).createItemRequest(any(NewItemRequest.class), eq(userId));
    }

    @Test
    void createItemRequest_shouldReturnBadRequest_whenUserIdMissing() throws Exception {
        NewItemRequest request = new NewItemRequest();
        request.setDescription("Нужна аккумуляторная дрель");

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllItemRequestsRequester_shouldReturnEmptyList() throws Exception {
        Long userId = 1L;

        when(itemRequestService.getAllItemRequestsRequester(userId)).thenReturn(List.of());

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(itemRequestService, times(1)).getAllItemRequestsRequester(userId);
    }

    @Test
    void getAll_shouldReturnEmptyList() throws Exception {
        Long userId = 1L;

        when(itemRequestService.getAll(userId)).thenReturn(List.of());

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(itemRequestService, times(1)).getAll(userId);
    }
}