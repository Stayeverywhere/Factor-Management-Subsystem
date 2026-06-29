package com.factor.common.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 业务异常测试
 */
class BusinessExceptionTest {

    @Test
    void testCreateWithMessage() {
        BusinessException exception = new BusinessException("Test error message");

        assertEquals("Test error message", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testCreateWithMessageAndCause() {
        Throwable cause = new RuntimeException("Original error");
        BusinessException exception = new BusinessException("Test error message", cause);

        assertEquals("Test error message", exception.getMessage());
        assertNotNull(exception.getCause());
        assertEquals("Original error", exception.getCause().getMessage());
    }

    @Test
    void testCreateWithEmptyMessage() {
        BusinessException exception = new BusinessException("");

        assertEquals("", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testCreateWithNullMessage() {
        BusinessException exception = new BusinessException(null);

        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testCreateWithNullCause() {
        BusinessException exception = new BusinessException("Test error", null);

        assertEquals("Test error", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testExceptionInheritance() {
        BusinessException exception = new BusinessException("Test error");

        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testThrowException() {
        assertThrows(BusinessException.class, () -> {
            throw new BusinessException("Test throw");
        });
    }

    @Test
    void testCatchException() {
        try {
            throw new BusinessException("Test catch");
        } catch (BusinessException e) {
            assertEquals("Test catch", e.getMessage());
        }
    }

    @Test
    void testExceptionWithNestedCause() {
        Throwable rootCause = new IllegalArgumentException("Root cause");
        Throwable intermediateCause = new RuntimeException("Intermediate cause", rootCause);
        BusinessException exception = new BusinessException("Business error", intermediateCause);

        assertEquals("Business error", exception.getMessage());
        assertNotNull(exception.getCause());
        assertEquals("Intermediate cause", exception.getCause().getMessage());
        assertNotNull(exception.getCause().getCause());
        assertEquals("Root cause", exception.getCause().getCause().getMessage());
    }

    @Test
    void testExceptionEquality() {
        BusinessException exception1 = new BusinessException("Test message");
        BusinessException exception2 = new BusinessException("Test message");

        assertEquals(exception1.getMessage(), exception2.getMessage());
    }

    @Test
    void testExceptionStackTrace() {
        BusinessException exception = new BusinessException("Test stacktrace");

        assertNotNull(exception.getStackTrace());
        assertTrue(exception.getStackTrace().length > 0);
    }

    @Test
    void testExceptionToString() {
        BusinessException exception = new BusinessException("Test toString");

        String exceptionString = exception.toString();
        assertNotNull(exceptionString);
        assertTrue(exceptionString.contains("BusinessException"));
        assertTrue(exceptionString.contains("Test toString"));
    }

    @Test
    void testExceptionWithLongMessage() {
        String longMessage = "This is a very long error message that contains a lot of text to test how the exception handles long messages properly";
        BusinessException exception = new BusinessException(longMessage);

        assertEquals(longMessage, exception.getMessage());
    }

    @Test
    void testExceptionWithSpecialCharacters() {
        String specialMessage = "Error: 中文测试 @$%^&*()_+{}[]|\\:;\"'<>,.?/~`";
        BusinessException exception = new BusinessException(specialMessage);

        assertEquals(specialMessage, exception.getMessage());
    }

    @Test
    void testExceptionCauseChain() {
        Throwable level1 = new RuntimeException("Level 1");
        Throwable level2 = new RuntimeException("Level 2", level1);
        Throwable level3 = new RuntimeException("Level 3", level2);

        BusinessException exception = new BusinessException("Top level", level3);

        assertEquals("Top level", exception.getMessage());
        assertNotNull(exception.getCause());
        assertEquals("Level 3", exception.getCause().getMessage());
    }
}