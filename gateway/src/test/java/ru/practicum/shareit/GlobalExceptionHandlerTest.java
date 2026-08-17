package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.exception.GlobalExceptionHandler;
import ru.practicum.shareit.exception.ValidationException;

import java.lang.reflect.Method;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleValidationException_shouldReturnErrorMap() {
        ValidationException ex = new ValidationException("Ошибка валидации");

        Map<String, String> response = handler.handleValidationException(ex);

        assertThat(response).containsEntry("error", "Ошибка валидации");
    }

    @Test
    void handleMethodArgumentNotValidException_shouldReturnErrorMap() throws NoSuchMethodException {
        Method method = TestController.class.getMethod("testMethod", String.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(null, "objectName");
        bindingResult.addError(new FieldError("objectName", "field", "Поле не может быть пустым"));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        Map<String, String> response = handler.handleValidationException(ex);

        assertThat(response).containsKey("error");
        assertThat(response.get("error")).isEqualTo("Поле не может быть пустым");
    }

    @Test
    void handleMethodArgumentNotValidException_shouldHandleMultipleErrors() throws NoSuchMethodException {
        Method method = TestController.class.getMethod("testMethod", String.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(null, "objectName");
        bindingResult.addError(new FieldError("objectName", "name", "Имя не может быть пустым"));
        bindingResult.addError(new FieldError("objectName", "email", "Email некорректный"));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        Map<String, String> response = handler.handleValidationException(ex);

        assertThat(response).containsKey("error");
        assertThat(response.get("error")).isEqualTo("Имя не может быть пустым");
    }

    // Контроллер для теста
    static class TestController {
        public void testMethod(@jakarta.validation.constraints.NotBlank String param) {
        }
    }
}