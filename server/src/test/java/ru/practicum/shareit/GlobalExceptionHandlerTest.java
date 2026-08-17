package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.*;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleDuplicateDataException_shouldReturnConflict() {
        DuplicateDataException ex = new DuplicateDataException("Пользователь уже существует");

        Map<String, String> response = handler.handleObjectDublicateData(ex);

        assertThat(response).containsKey("Ошибка валидации");
        assertThat(response.get("Ошибка валидации")).isEqualTo("Пользователь уже существует");
    }

    @Test
    void handleNotFoundException_shouldReturnNotFound() {
        NotFoundException ex = new NotFoundException("Объект не найден");

        Map<String, String> response = handler.handleObjectNotFound(ex);

        assertThat(response).containsKey("Ошибка валидации");
        assertThat(response.get("Ошибка валидации")).isEqualTo("Объект не найден");
    }

    @Test
    void handleAccessDeniedException_shouldReturnForbidden() {
        AccessDeniedException ex = new AccessDeniedException("Доступ запрещен");

        Map<String, String> response = handler.handleObjectAccessDenied(ex);

        assertThat(response).containsKey("Ошибка доступа");
        assertThat(response.get("Ошибка доступа")).isEqualTo("Доступ запрещен");
    }

    @Test
    void handleDataIntegrityViolationException_shouldReturnConflict() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Duplicate key");

        Map<String, String> response = handler.handleDataIntegrityViolation(ex);

        assertThat(response).containsKey("Ошибка при работе с БД");
        assertThat(response.get("Ошибка при работе с БД")).isEqualTo("Пользователь с таким email уже существует");
    }

    @Test
    void handleValidationException_shouldReturnBadRequest() {
        ValidationException ex = new ValidationException("Ошибка валидации");

        Map<String, String> response = handler.handleValidationException(ex);

        assertThat(response).containsKey("Ошибка валидации");
        assertThat(response.get("Ошибка валидации")).isEqualTo("Ошибка валидации");
    }

    @Test
    void handleItemNotAvailableException_shouldReturnBadRequest() {
        ItemNotAvailableException ex = new ItemNotAvailableException("Вещь недоступна");

        Map<String, String> response = handler.handleItemNotAvailableException(ex);

        assertThat(response).containsKey("Ошибка валидации");
        assertThat(response.get("Ошибка валидации")).isEqualTo("Вещь недоступна");
    }

    @Test
    void handleDuplicateDataException_shouldReturnResponseEntityWithMap() {
        DuplicateDataException ex = new DuplicateDataException("Пользователь уже существует");

        Map<String, String> response = handler.handleObjectDublicateData(ex);

        assertThat(response)
                .isInstanceOf(Map.class)
                .containsKey("Ошибка валидации")
                .containsValue("Пользователь уже существует");
    }

    @Test
    void handleNotFoundException_shouldReturnResponseEntityWithMap() {
        NotFoundException ex = new NotFoundException("Объект не найден");

        Map<String, String> response = handler.handleObjectNotFound(ex);

        assertThat(response)
                .isInstanceOf(Map.class)
                .containsKey("Ошибка валидации")
                .containsValue("Объект не найден");
    }

    @Test
    void handleValidationException_shouldReturnBadRequestWithMap() {
        ValidationException ex = new ValidationException("Ошибка валидации");

        Map<String, String> response = handler.handleValidationException(ex);

        assertThat(response)
                .isInstanceOf(Map.class)
                .containsKey("Ошибка валидации")
                .containsValue("Ошибка валидации");
    }

    @Test
    void handleAccessDeniedException_shouldReturnForbiddenWithMap() {
        AccessDeniedException ex = new AccessDeniedException("Доступ запрещен");

        Map<String, String> response = handler.handleObjectAccessDenied(ex);

        assertThat(response)
                .isInstanceOf(Map.class)
                .containsKey("Ошибка доступа")
                .containsValue("Доступ запрещен");
    }

    @Test
    void handleDataIntegrityViolation_shouldReturnConflictWithMap() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Duplicate email");

        Map<String, String> response = handler.handleDataIntegrityViolation(ex);

        assertThat(response)
                .isInstanceOf(Map.class)
                .containsKey("Ошибка при работе с БД")
                .containsValue("Пользователь с таким email уже существует");
    }

    @Test
    void handleItemNotAvailable_shouldReturnBadRequestWithMap() {
        ItemNotAvailableException ex = new ItemNotAvailableException("Вещь недоступна");

        Map<String, String> response = handler.handleItemNotAvailableException(ex);

        assertThat(response)
                .isInstanceOf(Map.class)
                .containsKey("Ошибка валидации")
                .containsValue("Вещь недоступна");
    }
}