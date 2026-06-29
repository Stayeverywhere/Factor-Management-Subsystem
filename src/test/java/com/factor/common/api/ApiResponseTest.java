package com.factor.common.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * API响应模型测试
 */
class ApiResponseTest {

    @Test
    void testOkWithData() {
        ApiResponse<String> response = ApiResponse.ok("test data");

        assertTrue(response.success());
        assertEquals("OK", response.message());
        assertEquals("test data", response.data());
        assertNull(response.traceId());
    }

    @Test
    void testOkWithMessage() {
        ApiResponse<String> response = ApiResponse.ok("Success", "test data");

        assertTrue(response.success());
        assertEquals("Success", response.message());
        assertEquals("test data", response.data());
        assertNull(response.traceId());
    }

    @Test
    void testFail() {
        ApiResponse<Void> response = ApiResponse.fail("Error occurred");

        assertFalse(response.success());
        assertEquals("Error occurred", response.message());
        assertNull(response.data());
        assertNull(response.traceId());
    }

    @Test
    void testOkWithNullData() {
        ApiResponse<Void> response = ApiResponse.ok(null);

        assertTrue(response.success());
        assertEquals("OK", response.message());
        assertNull(response.data());
        assertNull(response.traceId());
    }

    @Test
    void testOkWithComplexData() {
        TestData testData = new TestData("name", 123);
        ApiResponse<TestData> response = ApiResponse.ok(testData);

        assertTrue(response.success());
        assertEquals("OK", response.message());
        assertNotNull(response.data());
        assertEquals("name", response.data().name);
        assertEquals(123, response.data().value);
    }

    @Test
    void testOkWithEmptyMessage() {
        ApiResponse<String> response = ApiResponse.ok("", "test data");

        assertTrue(response.success());
        assertEquals("", response.message());
        assertEquals("test data", response.data());
    }

    @Test
    void testFailWithEmptyMessage() {
        ApiResponse<Void> response = ApiResponse.fail("");

        assertFalse(response.success());
        assertEquals("", response.message());
        assertNull(response.data());
    }

    @Test
    void testSuccessField() {
        ApiResponse<String> successResponse = ApiResponse.ok("data");
        ApiResponse<Void> failResponse = ApiResponse.fail("error");

        assertTrue(successResponse.success());
        assertFalse(failResponse.success());
    }

    @Test
    void testMessageField() {
        ApiResponse<String> okResponse = ApiResponse.ok("Success message", "data");
        ApiResponse<Void> failResponse = ApiResponse.fail("Error message");

        assertEquals("Success message", okResponse.message());
        assertEquals("Error message", failResponse.message());
    }

    @Test
    void testDataField() {
        ApiResponse<String> responseWithData = ApiResponse.ok("data");
        ApiResponse<Void> responseWithNullData = ApiResponse.ok(null);
        ApiResponse<Void> failResponse = ApiResponse.fail("error");

        assertEquals("data", responseWithData.data());
        assertNull(responseWithNullData.data());
        assertNull(failResponse.data());
    }

    @Test
    void testTraceIdField() {
        ApiResponse<String> response = ApiResponse.ok("data");

        assertNull(response.traceId());
    }

    @Test
    void testEquality() {
        ApiResponse<String> response1 = ApiResponse.ok("test data");
        ApiResponse<String> response2 = ApiResponse.ok("test data");

        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void testToString() {
        ApiResponse<String> response = ApiResponse.ok("test data");

        String responseString = response.toString();
        assertNotNull(responseString);
        assertTrue(responseString.contains("true"));
        assertTrue(responseString.contains("OK"));
        assertTrue(responseString.contains("test data"));
    }

    @Test
    void testOkWithListData() {
        ApiResponse<String[]> response = ApiResponse.ok(new String[]{"item1", "item2"});

        assertTrue(response.success());
        assertNotNull(response.data());
        assertEquals(2, response.data().length);
    }

    @Test
    void testOkWithIntegerData() {
        ApiResponse<Integer> response = ApiResponse.ok(123);

        assertTrue(response.success());
        assertEquals(123, response.data());
    }

    @Test
    void testOkWithBooleanData() {
        ApiResponse<Boolean> response = ApiResponse.ok(true);

        assertTrue(response.success());
        assertTrue(response.data());
    }

    private static class TestData {
        String name;
        int value;

        TestData(String name, int value) {
            this.name = name;
            this.value = value;
        }
    }
}