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
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.dto.StatusRequest;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingClient bookingClient;

    @Test
    void getBooking_shouldReturnBooking_whenExists() throws Exception {
        Long bookingId = 1L;
        Long userId = 1L;
        ResponseEntity<Object> response = ResponseEntity.ok().build();

        when(bookingClient.getBookingById(bookingId, userId)).thenReturn(response);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookingById(bookingId, userId);
    }

    @Test
    void getBookingAllForBooker_shouldReturnBookings_whenStateValid() throws Exception {
        Long userId = 1L;
        ResponseEntity<Object> response = ResponseEntity.ok().build();

        when(bookingClient.getBookingAllForBooker(eq(userId), any(StatusRequest.class))).thenReturn(response);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL"))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookingAllForBooker(eq(userId), any(StatusRequest.class));
    }

    @Test
    void getBookingAllForBooker_shouldReturnBadRequest_whenStateInvalid() throws Exception {
        Long userId = 1L;

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "INVALID_STATE"))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getBookingAllForBooker(anyLong(), any(StatusRequest.class));
    }

    @Test
    void getBookingAllForOwner_shouldReturnBookings_whenStateValid() throws Exception {
        Long userId = 1L;
        ResponseEntity<Object> response = ResponseEntity.ok().build();

        when(bookingClient.getBookingAllForOwner(eq(userId), any(StatusRequest.class))).thenReturn(response);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL"))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookingAllForOwner(eq(userId), any(StatusRequest.class));
    }

    @Test
    void createBooking_shouldReturnCreated_whenValidRequest() throws Exception {
        Long userId = 1L;
        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(1L);
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.CREATED).build();

        when(bookingClient.createBookingRequest(any(NewBookingRequest.class), eq(userId))).thenReturn(response);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(bookingClient, times(1)).createBookingRequest(any(NewBookingRequest.class), eq(userId));
    }

    @Test
    void createBooking_shouldReturnBadRequest_whenStartIsNull() throws Exception {
        Long userId = 1L;
        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(1L);
        request.setStart(null);
        request.setEnd(LocalDateTime.now().plusDays(2));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).createBookingRequest(any(NewBookingRequest.class), anyLong());
    }

    @Test
    void resolveBooking_shouldReturnOk_whenApproved() throws Exception {
        Long bookingId = 1L;
        Long userId = 1L;
        boolean approved = true;
        ResponseEntity<Object> response = ResponseEntity.ok().build();

        when(bookingClient.resolveBooking(bookingId, approved, userId)).thenReturn(response);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", String.valueOf(approved)))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).resolveBooking(bookingId, approved, userId);
    }

    @Test
    void resolveBooking_shouldReturnBadRequest_whenApprovedMissing() throws Exception {
        Long bookingId = 1L;
        Long userId = 1L;

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).resolveBooking(anyLong(), anyBoolean(), anyLong());
    }
}
