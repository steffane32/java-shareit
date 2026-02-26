package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SimpleTest {
    @Test
    public void testContext() {
        System.out.println("Контекст загружен!");
    }
}