package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ShareItTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        // Проверяем, что контекст поднялся
        assertThat(applicationContext).isNotNull();
    }

    @Test
    void mainMethodStarts() {
        // Проверяем, что main-метод запускается
        ShareItAppGateway.main(new String[]{});
    }
}
