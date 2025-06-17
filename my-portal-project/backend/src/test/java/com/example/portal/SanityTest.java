package com.example.portal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class SanityTest {

    @Test
    @DisplayName("JUnit5가 정상 작동하는지 확인하는 기본 테스트")
    void basicTest() {
        assertTrue(true);
        assertEquals(2, 1 + 1);
    }

    @Test
    @DisplayName("문자열 테스트")
    void stringTest() {
        String expected = "Hello";
        String actual = "Hello";
        assertEquals(expected, actual);
    }
}