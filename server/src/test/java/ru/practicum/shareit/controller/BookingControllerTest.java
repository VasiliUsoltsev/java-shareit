package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Test
    void getBooking_shouldReturnBooking() throws Exception {
        Long bookingId = 1L;
        Long userId = 1L;

        BookingResponse response = new BookingResponse();
        response.setId(bookingId);
        response.setStatus(Status.WAITING);

        when(bookingService.getBookingById(bookingId, userId)).thenReturn(response);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("WAITING"));

        verify(bookingService, times(1)).getBookingById(bookingId, userId);
    }

    @Test
    void getBookingAllForBooker_shouldReturnBookings() throws Exception {
        Long userId = 1L;
        String state = "ALL";

        BookingResponse response1 = new BookingResponse();
        response1.setId(1L);

        BookingResponse response2 = new BookingResponse();
        response2.setId(2L);

        when(bookingService.getBookingAllForBooker(userId, state)).thenReturn(List.of(response1, response2));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));

        verify(bookingService, times(1)).getBookingAllForBooker(userId, state);
    }

    @Test
    void getBookingAllForBooker_shouldUseDefaultState() throws Exception {
        Long userId = 1L;

        when(bookingService.getBookingAllForBooker(eq(userId), eq("ALL"))).thenReturn(List.of());

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingService, times(1)).getBookingAllForBooker(userId, "ALL");
    }

    @Test
    void getBookingAllForOwner_shouldReturnBookings() throws Exception {
        Long userId = 1L;
        String state = "ALL";

        BookingResponse response1 = new BookingResponse();
        response1.setId(1L);

        when(bookingService.getBookingAllForOwner(userId, state)).thenReturn(List.of(response1));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(bookingService, times(1)).getBookingAllForOwner(userId, state);
    }

    @Test
    void getBookingAllForOwner_shouldUseDefaultState() throws Exception {
        Long userId = 1L;

        when(bookingService.getBookingAllForOwner(eq(userId), eq("ALL"))).thenReturn(List.of());

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingService, times(1)).getBookingAllForOwner(userId, "ALL");
    }

    @Test
    void createBooking_shouldReturnCreated() throws Exception {
        Long userId = 1L;
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(1L);
        request.setStart(start);
        request.setEnd(end);

        BookingResponse response = new BookingResponse();
        response.setId(1L);
        response.setStatus(Status.WAITING);

        when(bookingService.createBookingRequest(any(NewBookingRequest.class), eq(userId))).thenReturn(response);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("WAITING"));

        verify(bookingService, times(1)).createBookingRequest(any(NewBookingRequest.class), eq(userId));
    }

    @Test
    void resolveBooking_shouldReturnOk() throws Exception {
        Long bookingId = 1L;
        Long userId = 1L;
        boolean approved = true;

        BookingResponse response = new BookingResponse();
        response.setId(bookingId);
        response.setStatus(Status.APPROVED);

        when(bookingService.resolveBooking(bookingId, approved, userId)).thenReturn(response);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", String.valueOf(approved)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(bookingService, times(1)).resolveBooking(bookingId, approved, userId);
    }

    @Test
    void resolveBooking_shouldReturnRejected() throws Exception {
        Long bookingId = 1L;
        Long userId = 1L;
        boolean approved = false;

        BookingResponse response = new BookingResponse();
        response.setId(bookingId);
        response.setStatus(Status.CANCELED);

        when(bookingService.resolveBooking(bookingId, approved, userId)).thenReturn(response);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", String.valueOf(approved)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("CANCELED"));

        verify(bookingService, times(1)).resolveBooking(bookingId, approved, userId);
    }

    @Test
    void getBooking_shouldReturnBadRequest_whenUserIdMissing() throws Exception {
        mockMvc.perform(get("/bookings/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingAllForBooker_shouldReturnBadRequest_whenUserIdMissing() throws Exception {
        mockMvc.perform(get("/bookings"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingAllForOwner_shouldReturnBadRequest_whenUserIdMissing() throws Exception {
        mockMvc.perform(get("/bookings/owner"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBooking_shouldReturnBadRequest_whenUserIdMissing() throws Exception {
        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(1L);
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void resolveBooking_shouldReturnBadRequest_whenUserIdMissing() throws Exception {
        mockMvc.perform(patch("/bookings/1?approved=true"))
                .andExpect(status().isBadRequest());
    }
}