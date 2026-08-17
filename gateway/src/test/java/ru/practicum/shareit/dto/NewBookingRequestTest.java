package ru.practicum.shareit.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.NewBookingRequest;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class NewBookingRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldValidateValidBooking() {
        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(1L);
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        Set<ConstraintViolation<NewBookingRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailWhenItemIdIsNull() {
        NewBookingRequest request = new NewBookingRequest();
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        Set<ConstraintViolation<NewBookingRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                .contains("itemId");
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("Нельза произвести бронирование без указания вещи");
    }

    @Test
    void shouldFailWhenStartIsNull() {
        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(1L);
        request.setEnd(LocalDateTime.now().plusDays(2));

        Set<ConstraintViolation<NewBookingRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                .contains("start");
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("Дата начала аренды не может быть пустой");
    }

    @Test
    void shouldFailWhenEndIsNull() {
        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(1L);
        request.setStart(LocalDateTime.now().plusDays(1));

        Set<ConstraintViolation<NewBookingRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                .contains("end");
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("Дата завершения аренды не может быть пустой");
    }

    @Test
    void shouldFailWhenStartIsInPast() {
        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(1L);
        request.setStart(LocalDateTime.now().minusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        Set<ConstraintViolation<NewBookingRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                .contains("start");
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("Дата начала аренды не может быть в прошлом");
    }

    @Test
    void shouldFailWhenEndIsInPast() {
        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(1L);
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().minusDays(1));

        Set<ConstraintViolation<NewBookingRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                .contains("end");
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("Дата завершения аренды не может быть в прошлом");
    }

    @Test
    void shouldFailWhenEndIsNow() {
        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(1L);
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now());

        Set<ConstraintViolation<NewBookingRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                .contains("end");
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("Дата завершения аренды не может быть в прошлом");
    }

    @Test
    void shouldFailWhenAllFieldsAreInvalid() {
        NewBookingRequest request = new NewBookingRequest();

        Set<ConstraintViolation<NewBookingRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).hasSize(3);
        assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                .contains("itemId", "start", "end");
    }
}