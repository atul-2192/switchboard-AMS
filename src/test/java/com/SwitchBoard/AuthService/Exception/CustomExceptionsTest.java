package com.SwitchBoard.AuthService.Exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Custom Exception Tests")
class CustomExceptionsTest {

    @Test
    @DisplayName("Should create ResourceNotFoundException with message")
    void testResourceNotFoundException() {
        // Arrange
        String message = "Resource not found";

        // Act
        ResourceNotFoundException exception = new ResourceNotFoundException(message);

        // Assert
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("Should create BadRequestException with message")
    void testBadRequestException() {
        // Arrange
        String message = "Bad request";

        // Act
        BadRequestException exception = new BadRequestException(message);

        // Assert
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("Should create UnauthorizedException with message")
    void testUnauthorizedException() {
        // Arrange
        String message = "Unauthorized";

        // Act
        UnauthorizedException exception = new UnauthorizedException(message);

        // Assert
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("Should create UnexpectedException with message")
    void testUnexpectedException() {
        // Arrange
        String message = "Unexpected error";

        // Act
        UnexpectedException exception = new UnexpectedException(message);

        // Assert
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("Should throw and catch ResourceNotFoundException")
    void testThrowResourceNotFoundException() {
        // Arrange
        String message = "User not found";

        // Act & Assert
        Exception thrown = assertThrows(ResourceNotFoundException.class, () -> {
            throw new ResourceNotFoundException(message);
        });

        assertEquals(message, thrown.getMessage());
    }

    @Test
    @DisplayName("Should throw and catch BadRequestException")
    void testThrowBadRequestException() {
        // Arrange
        String message = "Invalid input";

        // Act & Assert
        Exception thrown = assertThrows(BadRequestException.class, () -> {
            throw new BadRequestException(message);
        });

        assertEquals(message, thrown.getMessage());
    }

    @Test
    @DisplayName("Should throw and catch UnauthorizedException")
    void testThrowUnauthorizedException() {
        // Arrange
        String message = "Invalid credentials";

        // Act & Assert
        Exception thrown = assertThrows(UnauthorizedException.class, () -> {
            throw new UnauthorizedException(message);
        });

        assertEquals(message, thrown.getMessage());
    }

    @Test
    @DisplayName("Should throw and catch UnexpectedException")
    void testThrowUnexpectedException() {
        // Arrange
        String message = "System error";

        // Act & Assert
        Exception thrown = assertThrows(UnexpectedException.class, () -> {
            throw new UnexpectedException(message);
        });

        assertEquals(message, thrown.getMessage());
    }

    @Test
    @DisplayName("Should support exception chaining for ResourceNotFoundException")
    void testResourceNotFoundExceptionChaining() {
        // Arrange
        String message = "Resource not found";

        // Act
        ResourceNotFoundException exception = new ResourceNotFoundException(message);

        // Assert
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("Should distinguish between different exception types")
    void testExceptionTypeDistinction() {
        // Act
        ResourceNotFoundException resourceNotFound = new ResourceNotFoundException("Not found");
        BadRequestException badRequest = new BadRequestException("Bad request");
        UnauthorizedException unauthorized = new UnauthorizedException("Unauthorized");
        UnexpectedException unexpected = new UnexpectedException("Unexpected");

        // Assert
        assertNotEquals(resourceNotFound.getClass(), badRequest.getClass());
        assertNotEquals(badRequest.getClass(), unauthorized.getClass());
        assertNotEquals(unauthorized.getClass(), unexpected.getClass());
        assertNotEquals(unexpected.getClass(), resourceNotFound.getClass());
    }
}
