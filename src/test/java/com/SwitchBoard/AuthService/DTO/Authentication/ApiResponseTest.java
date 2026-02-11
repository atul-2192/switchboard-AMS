package com.SwitchBoard.AuthService.DTO.Authentication;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("API Response DTO Test")
class ApiResponseTest {

    @Test
    @DisplayName("Should create success response with message and success flag")
    void testSuccessResponse() {
        // Act
        ApiResponse response = ApiResponse.success("Operation successful", true);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Operation successful", response.getMessage());
        assertNull(response.getData());
        assertNull(response.getErrorCode());
        assertNull(response.getPath());
        assertNotNull(response.getTimestamp());
    }

    @Test
    @DisplayName("Should create success response with data and path")
    void testSuccessResponseWithData() {
        // Arrange
        Object data = "test-data";
        String path = "/api/v1/test";

        // Act
        ApiResponse response = ApiResponse.success("Success", data, path);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Success", response.getMessage());
        assertEquals(data, response.getData());
        assertEquals(path, response.getPath());
        assertNull(response.getErrorCode());
        assertNotNull(response.getTimestamp());
    }

    @Test
    @DisplayName("Should create error response")
    void testErrorResponse() {
        // Arrange
        String message = "Error occurred";
        String errorCode = "ERR_001";
        String path = "/api/v1/error";

        // Act
        ApiResponse response = ApiResponse.error(message, errorCode, path);

        // Assert
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertEquals(errorCode, response.getErrorCode());
        assertEquals(path, response.getPath());
        assertNull(response.getData());
        assertNotNull(response.getTimestamp());
    }

    @Test
    @DisplayName("Should set timestamp on creation")
    void testTimestampCreation() {
        // Arrange
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        // Act
        ApiResponse response = ApiResponse.success("Test", true);
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);

        // Assert
        assertNotNull(response.getTimestamp());
        assertTrue(response.getTimestamp().isAfter(before));
        assertTrue(response.getTimestamp().isBefore(after));
    }

    @Test
    @DisplayName("Should support all-args constructor")
    void testAllArgsConstructor() {
        // Arrange
        LocalDateTime timestamp = LocalDateTime.now();

        // Act
        ApiResponse response = new ApiResponse(
                true, "Test message", "data", "CODE", timestamp, "/path"
        );

        // Assert
        assertTrue(response.isSuccess());
        assertEquals("Test message", response.getMessage());
        assertEquals("data", response.getData());
        assertEquals("CODE", response.getErrorCode());
        assertEquals(timestamp, response.getTimestamp());
        assertEquals("/path", response.getPath());
    }

    @Test
    @DisplayName("Should support no-args constructor and setters")
    void testNoArgsConstructorAndSetters() {
        // Arrange
        ApiResponse response = new ApiResponse();
        LocalDateTime timestamp = LocalDateTime.now();

        // Act
        response.setSuccess(true);
        response.setMessage("Test");
        response.setData("data");
        response.setErrorCode("CODE");
        response.setTimestamp(timestamp);
        response.setPath("/path");

        // Assert
        assertTrue(response.isSuccess());
        assertEquals("Test", response.getMessage());
        assertEquals("data", response.getData());
        assertEquals("CODE", response.getErrorCode());
        assertEquals(timestamp, response.getTimestamp());
        assertEquals("/path", response.getPath());
    }

    @Test
    @DisplayName("Should handle complex data objects")
    void testComplexDataObject() {
        // Arrange
        String complexData = "{\"field1\":\"value1\",\"field2\":42}";

        // Act
        ApiResponse response = ApiResponse.success("Success", complexData, "/api");

        // Assert
        assertNotNull(response.getData());
        assertEquals(complexData, response.getData());
    }

    @Test
    @DisplayName("Should create different error responses")
    void testDifferentErrorCodes() {
        // Act
        ApiResponse response1 = ApiResponse.error("Not found", "NOT_FOUND", "/api/1");
        ApiResponse response2 = ApiResponse.error("Bad request", "BAD_REQUEST", "/api/2");
        ApiResponse response3 = ApiResponse.error("Unauthorized", "UNAUTHORIZED", "/api/3");

        // Assert
        assertEquals("NOT_FOUND", response1.getErrorCode());
        assertEquals("BAD_REQUEST", response2.getErrorCode());
        assertEquals("UNAUTHORIZED", response3.getErrorCode());
    }
}
