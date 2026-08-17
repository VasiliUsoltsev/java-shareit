package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.dto.StatusRequest;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingClientTest {

    private BookingClient bookingClient;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() throws Exception {
        // Создаем реальный RestTemplateBuilder
        RestTemplateBuilder builder = new RestTemplateBuilder();

        // Создаем BookingClient с реальным builder, но подменяем rest через рефлексию
        bookingClient = new BookingClient("http://localhost:8081", builder);

        // Подменяем rest на mock через рефлексию
        Field restField = BaseClient.class.getDeclaredField("rest");
        restField.setAccessible(true);
        restField.set(bookingClient, restTemplate);
    }

    @Test
    void createBookingRequest_shouldSendPostRequest() {
        Long userId = 1L;
        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(1L);
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        ResponseEntity<Object> expectedResponse = ResponseEntity.status(HttpStatus.CREATED).build();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.createBookingRequest(request, userId);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.POST), any(), eq(Object.class));
    }

    @Test
    void resolveBooking_shouldSendPatchRequestWithApprovedParam() {
        Long bookingId = 1L;
        Long userId = 1L;
        boolean approved = true;

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.PATCH), any(), eq(Object.class), any(Map.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.resolveBooking(bookingId, approved, userId);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(restTemplate, times(1)).exchange(
                anyString(),
                eq(HttpMethod.PATCH),
                any(),
                eq(Object.class),
                any(Map.class)
        );
    }

    @Test
    void resolveBooking_shouldSendPatchRequestWithRejectedParam() {
        Long bookingId = 1L;
        Long userId = 1L;
        boolean approved = false;

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.PATCH), any(), eq(Object.class), any(Map.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.resolveBooking(bookingId, approved, userId);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(restTemplate, times(1)).exchange(
                anyString(),
                eq(HttpMethod.PATCH),
                any(),
                eq(Object.class),
                any(Map.class)
        );
    }

    @Test
    void getBookingById_shouldSendGetRequest() {
        Long bookingId = 1L;
        Long userId = 1L;

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.getBookingById(bookingId, userId);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), any(), eq(Object.class));
    }

    @Test
    void getBookingAllForBooker_shouldSendGetRequestWithStateParam() {
        Long userId = 1L;
        StatusRequest state = StatusRequest.ALL;

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Object.class), any(Map.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.getBookingAllForBooker(userId, state);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(restTemplate, times(1)).exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class),
                any(Map.class)
        );
    }

    @Test
    void getBookingAllForBooker_shouldSendGetRequestWithDifferentStates() {
        Long userId = 1L;

        for (StatusRequest state : StatusRequest.values()) {
            ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
            when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Object.class), any(Map.class)))
                    .thenReturn(expectedResponse);

            ResponseEntity<Object> response = bookingClient.getBookingAllForBooker(userId, state);

            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        verify(restTemplate, times(StatusRequest.values().length)).exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class),
                any(Map.class)
        );
    }

    @Test
    void getBookingAllForOwner_shouldSendGetRequestWithStateParam() {
        Long userId = 1L;
        StatusRequest state = StatusRequest.ALL;

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Object.class), any(Map.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.getBookingAllForOwner(userId, state);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(restTemplate, times(1)).exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class),
                any(Map.class)
        );
    }

    @Test
    void getBookingAllForOwner_shouldSendGetRequestWithDifferentStates() {
        Long userId = 1L;

        for (StatusRequest state : StatusRequest.values()) {
            ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
            when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Object.class), any(Map.class)))
                    .thenReturn(expectedResponse);

            ResponseEntity<Object> response = bookingClient.getBookingAllForOwner(userId, state);

            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        verify(restTemplate, times(StatusRequest.values().length)).exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class),
                any(Map.class)
        );
    }
}