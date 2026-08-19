package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingItemResponse;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void getAll_shouldReturnItems() throws Exception {
        Long userId = 1L;

        ItemBookingDateResponse response1 = new ItemBookingDateResponse();
        response1.setId(1L);
        response1.setName("Дрель");

        ItemBookingDateResponse response2 = new ItemBookingDateResponse();
        response2.setId(2L);
        response2.setName("Отвертка");

        when(itemService.getAll(userId)).thenReturn(List.of(response1, response2));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Дрель"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Отвертка"));

        verify(itemService, times(1)).getAll(userId);
    }

    @Test
    void getAll_shouldReturnBadRequest_whenUserIdMissing() throws Exception {
        mockMvc.perform(get("/items"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void get_shouldReturnItem() throws Exception {
        Long itemId = 1L;
        Long userId = 1L;

        ItemBookingDateResponse response = new ItemBookingDateResponse();
        response.setId(itemId);
        response.setName("Дрель");
        response.setDescription("Аккумуляторная дрель");
        response.setAvailable(true);

        when(itemService.getItem(itemId, userId)).thenReturn(response);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Дрель"))
                .andExpect(jsonPath("$.description").value("Аккумуляторная дрель"))
                .andExpect(jsonPath("$.available").value(true));

        verify(itemService, times(1)).getItem(itemId, userId);
    }

    @Test
    void get_shouldReturnBadRequest_whenUserIdMissing() throws Exception {
        mockMvc.perform(get("/items/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void search_shouldReturnItems() throws Exception {
        Long userId = 1L;
        String text = "дрель";

        ItemResponse response1 = new ItemResponse();
        response1.setId(1L);
        response1.setName("Дрель");

        when(itemService.searchItem(text, userId)).thenReturn(List.of(response1));

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", text))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Дрель"));

        verify(itemService, times(1)).searchItem(text, userId);
    }

    @Test
    void search_shouldReturnEmptyList_whenTextIsEmpty() throws Exception {
        Long userId = 1L;
        String text = "";

        when(itemService.searchItem(text, userId)).thenReturn(List.of());

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", text))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(itemService, times(1)).searchItem(text, userId);
    }

    @Test
    void search_shouldReturnBadRequest_whenUserIdMissing() throws Exception {
        mockMvc.perform(get("/items/search")
                        .param("text", "дрель"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_shouldReturnCreated() throws Exception {
        Long userId = 1L;

        NewItemRequest request = new NewItemRequest();
        request.setName("Дрель");
        request.setDescription("Аккумуляторная дрель");
        request.setAvailable(true);

        ItemResponse response = new ItemResponse();
        response.setId(1L);
        response.setName("Дрель");
        response.setDescription("Аккумуляторная дрель");
        response.setAvailable(true);

        when(itemService.createItem(any(NewItemRequest.class), eq(userId))).thenReturn(response);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Дрель"))
                .andExpect(jsonPath("$.description").value("Аккумуляторная дрель"))
                .andExpect(jsonPath("$.available").value(true));

        verify(itemService, times(1)).createItem(any(NewItemRequest.class), eq(userId));
    }

    @Test
    void create_shouldReturnBadRequest_whenUserIdMissing() throws Exception {
        NewItemRequest request = new NewItemRequest();
        request.setName("Дрель");
        request.setDescription("Аккумуляторная дрель");
        request.setAvailable(true);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addComment_shouldReturnCreated() throws Exception {
        Long itemId = 1L;
        Long userId = 1L;

        NewCommentRequest request = new NewCommentRequest();
        request.setText("Отличная вещь!");

        CommentResponse response = new CommentResponse();
        response.setId(1L);
        response.setText("Отличная вещь!");
        response.setAuthorName("Анна Смирнова");
        response.setCreated(LocalDateTime.now());

        when(itemService.addComment(any(NewCommentRequest.class), eq(itemId), eq(userId))).thenReturn(response);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Отличная вещь!"))
                .andExpect(jsonPath("$.authorName").value("Анна Смирнова"));

        verify(itemService, times(1)).addComment(any(NewCommentRequest.class), eq(itemId), eq(userId));
    }

    @Test
    void addComment_shouldReturnBadRequest_whenUserIdMissing() throws Exception {
        NewCommentRequest request = new NewCommentRequest();
        request.setText("Отличная вещь!");

        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_shouldReturnOk() throws Exception {
        Long itemId = 1L;
        Long userId = 1L;

        UpdateItemPatchRequest request = new UpdateItemPatchRequest();
        request.setName("Обновленная дрель");
        request.setDescription("Обновленное описание");
        request.setAvailable(false);

        ItemResponse response = new ItemResponse();
        response.setId(itemId);
        response.setName("Обновленная дрель");
        response.setDescription("Обновленное описание");
        response.setAvailable(false);

        when(itemService.updateItem(eq(itemId), any(UpdateItemPatchRequest.class), eq(userId))).thenReturn(response);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Обновленная дрель"))
                .andExpect(jsonPath("$.description").value("Обновленное описание"))
                .andExpect(jsonPath("$.available").value(false));

        verify(itemService, times(1)).updateItem(eq(itemId), any(UpdateItemPatchRequest.class), eq(userId));
    }

    @Test
    void update_shouldReturnBadRequest_whenUserIdMissing() throws Exception {
        UpdateItemPatchRequest request = new UpdateItemPatchRequest();
        request.setName("Обновленная дрель");

        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        Long itemId = 1L;
        Long userId = 1L;

        doNothing().when(itemService).removeItem(itemId, userId);

        mockMvc.perform(delete("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNoContent());

        verify(itemService, times(1)).removeItem(itemId, userId);
    }

    @Test
    void delete_shouldReturnBadRequest_whenUserIdMissing() throws Exception {
        mockMvc.perform(delete("/items/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void get_shouldReturnItemWithBookingDates() throws Exception {
        Long itemId = 1L;
        Long userId = 1L;

        ItemBookingDateResponse response = new ItemBookingDateResponse();
        response.setId(itemId);
        response.setName("Дрель");
        response.setDescription("Аккумуляторная дрель");
        response.setAvailable(true);
        response.setLastBooking(new BookingItemResponse());
        response.setNextBooking(new BookingItemResponse());

        when(itemService.getItem(itemId, userId)).thenReturn(response);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastBooking").exists())
                .andExpect(jsonPath("$.nextBooking").exists());

        verify(itemService, times(1)).getItem(itemId, userId);
    }

    @Test
    void getAll_shouldReturnItemsWithBookingDates() throws Exception {
        Long userId = 1L;

        ItemBookingDateResponse response = new ItemBookingDateResponse();
        response.setId(1L);
        response.setName("Дрель");

        // Создаем и заполняем объекты через сеттеры
        BookingItemResponse lastBooking = new BookingItemResponse();
        lastBooking.setId(10L);
        lastBooking.setStatus(Status.WAITING);
        response.setLastBooking(lastBooking);

        BookingItemResponse nextBooking = new BookingItemResponse();
        nextBooking.setId(11L);
        nextBooking.setStatus(Status.APPROVED);
        response.setNextBooking(nextBooking);

        when(itemService.getAll(userId)).thenReturn(List.of(response));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lastBooking").exists())
                .andExpect(jsonPath("$[0].nextBooking").exists());

        verify(itemService, times(1)).getAll(userId);
    }
}