package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.StatusRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class StatusRequestTest {

    @Test
    void from_shouldReturnStatus_whenValidUpperCase() {
        Optional<StatusRequest> result = StatusRequest.from("ALL");

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(StatusRequest.ALL);
    }

    @Test
    void from_shouldReturnStatus_whenValidLowerCase() {
        Optional<StatusRequest> result = StatusRequest.from("all");

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(StatusRequest.ALL);
    }

    @Test
    void from_shouldReturnStatus_whenValidMixedCase() {
        Optional<StatusRequest> result = StatusRequest.from("CuRrEnT");

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(StatusRequest.CURRENT);
    }

    @Test
    void from_shouldReturnAllStatuses() {
        assertThat(StatusRequest.from("ALL")).contains(StatusRequest.ALL);
        assertThat(StatusRequest.from("CURRENT")).contains(StatusRequest.CURRENT);
        assertThat(StatusRequest.from("PAST")).contains(StatusRequest.PAST);
        assertThat(StatusRequest.from("FUTURE")).contains(StatusRequest.FUTURE);
        assertThat(StatusRequest.from("WAITING")).contains(StatusRequest.WAITING);
        assertThat(StatusRequest.from("REJECTED")).contains(StatusRequest.REJECTED);
    }

    @Test
    void from_shouldReturnEmpty_whenInvalidString() {
        Optional<StatusRequest> result = StatusRequest.from("INVALID");

        assertThat(result).isEmpty();
    }

    @Test
    void from_shouldReturnEmpty_whenEmptyString() {
        Optional<StatusRequest> result = StatusRequest.from("");

        assertThat(result).isEmpty();
    }

    @Test
    void from_shouldReturnEmpty_whenNull() {
        Optional<StatusRequest> result = StatusRequest.from(null);

        assertThat(result).isEmpty();
    }

    @Test
    void values_shouldContainAllStatuses() {
        StatusRequest[] values = StatusRequest.values();

        assertThat(values).containsExactlyInAnyOrder(
                StatusRequest.ALL,
                StatusRequest.CURRENT,
                StatusRequest.PAST,
                StatusRequest.FUTURE,
                StatusRequest.WAITING,
                StatusRequest.REJECTED
        );
    }

    @Test
    void valueOf_shouldReturnStatus_whenValid() {
        assertThat(StatusRequest.valueOf("ALL")).isEqualTo(StatusRequest.ALL);
        assertThat(StatusRequest.valueOf("WAITING")).isEqualTo(StatusRequest.WAITING);
        assertThat(StatusRequest.valueOf("REJECTED")).isEqualTo(StatusRequest.REJECTED);
    }
}