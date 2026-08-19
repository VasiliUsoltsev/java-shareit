package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemPatchRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    @Test
    void getAll_shouldReturnItems() throws Exception {
        Long userId = 1L;
        ResponseEntity<Object> response = ResponseEntity.ok().build();

        when(itemClient.getAll(userId)).thenReturn(response);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).getAll(userId);
    }

    @Test
    void getAll_shouldReturnBadRequest_whenUserIdMissing() throws Exception {
        mockMvc.perform(get("/items"))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getAll(anyLong());
    }

    @Test
    void get_shouldReturnItem_whenExists() throws Exception {
        Long itemId = 1L;
        Long userId = 1L;
        ResponseEntity<Object> response = ResponseEntity.ok().build();

        when(itemClient.getItem(itemId, userId)).thenReturn(response);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).getItem(itemId, userId);
    }

    @Test
    void get_shouldReturnBadRequest_whenUserIdMissing() throws Exception {
        Long itemId = 1L;

        mockMvc.perform(get("/items/{itemId}", itemId))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getItem(anyLong(), anyLong());
    }

    @Test
    void search_shouldReturnItems_whenTextProvided() throws Exception {
        Long userId = 1L;
        String text = "дрель";
        ResponseEntity<Object> response = ResponseEntity.ok().build();

        when(itemClient.searchItem(text, userId)).thenReturn(response);

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", text))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).searchItem(text, userId);
    }

    @Test
    void search_shouldReturnBadRequest_whenUserIdMissing() throws Exception {
        mockMvc.perform(get("/items/search")
                        .param("text", "дрель"))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).searchItem(anyString(), anyLong());
    }

    @Test
    void search_shouldReturnOk_whenTextIsEmpty() throws Exception {
        Long userId = 1L;
        ResponseEntity<Object> response = ResponseEntity.ok().build();

        when(itemClient.searchItem("", userId)).thenReturn(response);

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", ""))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).searchItem("", userId);
    }

    @Test
    void create_shouldReturnCreated_whenValidRequest() throws Exception {
        Long userId = 1L;
        NewItemRequest request = new NewItemRequest();
        request.setName("Дрель");
        request.setDescription("Мощная аккумуляторная дрель");
        request.setAvailable(true);

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.CREATED).build();

        when(itemClient.createItem(any(NewItemRequest.class), eq(userId))).thenReturn(response);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(itemClient, times(1)).createItem(any(NewItemRequest.class), eq(userId));
    }

    @Test
    void create_shouldReturnBadRequest_whenNameIsNull() throws Exception {
        Long userId = 1L;
        NewItemRequest request = new NewItemRequest();
        request.setDescription("Мощная аккумуляторная дрель");
        request.setAvailable(true);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createItem(any(NewItemRequest.class), anyLong());
    }

    @Test
    void create_shouldReturnBadRequest_whenDescriptionIsNull() throws Exception {
        Long userId = 1L;
        NewItemRequest request = new NewItemRequest();
        request.setName("Дрель");
        request.setAvailable(true);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createItem(any(NewItemRequest.class), anyLong());
    }

    @Test
    void create_shouldReturnBadRequest_whenAvailableIsNull() throws Exception {
        Long userId = 1L;
        NewItemRequest request = new NewItemRequest();
        request.setName("Дрель");
        request.setDescription("Мощная аккумуляторная дрель");

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createItem(any(NewItemRequest.class), anyLong());
    }

    @Test
    void create_shouldReturnBadRequest_whenUserIdMissing() throws Exception {
        NewItemRequest request = new NewItemRequest();
        request.setName("Дрель");
        request.setDescription("Мощная аккумуляторная дрель");
        request.setAvailable(true);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createItem(any(NewItemRequest.class), anyLong());
    }

    @Test
    void addComment_shouldReturnCreated_whenValidRequest() throws Exception {
        Long itemId = 1L;
        Long userId = 1L;
        NewCommentRequest request = new NewCommentRequest();
        request.setText("Отличная вещь, очень понравилась!");

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.CREATED).build();

        when(itemClient.addComment(any(NewCommentRequest.class), eq(itemId), eq(userId))).thenReturn(response);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(itemClient, times(1)).addComment(any(NewCommentRequest.class), eq(itemId), eq(userId));
    }

    @Test
    void addComment_shouldReturnBadRequest_whenTextIsNull() throws Exception {
        Long itemId = 1L;
        Long userId = 1L;
        NewCommentRequest request = new NewCommentRequest();

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).addComment(any(NewCommentRequest.class), anyLong(), anyLong());
    }

    @Test
    void addComment_shouldReturnBadRequest_whenTextIsEmpty() throws Exception {
        Long itemId = 1L;
        Long userId = 1L;
        NewCommentRequest request = new NewCommentRequest();
        request.setText("");

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).addComment(any(NewCommentRequest.class), anyLong(), anyLong());
    }

    @Test
    void update_shouldReturnOk_whenValidRequest() throws Exception {
        Long itemId = 1L;
        Long userId = 1L;
        UpdateItemPatchRequest request = new UpdateItemPatchRequest();
        request.setName("Обновленная дрель");
        request.setDescription("Еще более мощная дрель");

        ResponseEntity<Object> response = ResponseEntity.ok().build();

        when(itemClient.updateItem(eq(itemId), any(UpdateItemPatchRequest.class), eq(userId))).thenReturn(response);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).updateItem(eq(itemId), any(UpdateItemPatchRequest.class), eq(userId));
    }

    @Test
    void update_shouldReturnBadRequest_whenUserIdMissing() throws Exception {
        Long itemId = 1L;
        UpdateItemPatchRequest request = new UpdateItemPatchRequest();
        request.setName("Обновленная дрель");

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).updateItem(anyLong(), any(UpdateItemPatchRequest.class), anyLong());
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        Long itemId = 1L;
        Long userId = 1L;

        doNothing().when(itemClient).removeItem(itemId, userId);

        mockMvc.perform(delete("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNoContent());

        verify(itemClient, times(1)).removeItem(itemId, userId);
    }

    @Test
    void delete_shouldReturnBadRequest_whenUserIdMissing() throws Exception {
        Long itemId = 1L;

        mockMvc.perform(delete("/items/{itemId}", itemId))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).removeItem(anyLong(), anyLong());
    }
}
