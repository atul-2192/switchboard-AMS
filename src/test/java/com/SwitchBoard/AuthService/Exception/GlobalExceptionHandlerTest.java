package com.SwitchBoard.AuthService.Exception;

import com.SwitchBoard.AuthService.DTO.Authentication.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Global Exception Handler Test")
class GlobalExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    private static final String TEST_URI = "/api/v1/test";

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn(TEST_URI);
    }

    @Test
    @DisplayName("Should handle ResourceNotFoundException")
    void testHandleResourceNotFoundException() {
        // Arrange
        String errorMessage = "Resource not found";
        ResourceNotFoundException exception = new ResourceNotFoundException(errorMessage);

        // Act
        ResponseEntity<ApiResponse> response = globalExceptionHandler.handleResourceNotFound(exception, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertEquals("RESOURCE_NOT_FOUND", response.getBody().getErrorCode());
        assertEquals(TEST_URI, response.getBody().getPath());
    }

    @Test
    @DisplayName("Should handle BadRequestException")
    void testHandleBadRequestException() {
        // Arrange
        String errorMessage = "Bad request";
        BadRequestException exception = new BadRequestException(errorMessage);

        // Act
        ResponseEntity<ApiResponse> response = globalExceptionHandler.handleBadRequest(exception, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertEquals("BAD_REQUEST", response.getBody().getErrorCode());
        assertEquals(TEST_URI, response.getBody().getPath());
    }

    @Test
    @DisplayName("Should handle UnauthorizedException")
    void testHandleUnauthorizedException() {
        // Arrange
        String errorMessage = "Unauthorized access";
        UnauthorizedException exception = new UnauthorizedException(errorMessage);

        // Act
        ResponseEntity<ApiResponse> response = globalExceptionHandler.handleUnauthorized(exception, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertEquals("UNAUTHORIZED", response.getBody().getErrorCode());
        assertEquals(TEST_URI, response.getBody().getPath());
    }

    @Test
    @DisplayName("Should handle UnexpectedException")
    void testHandleUnexpectedException() {
        // Arrange
        String errorMessage = "Unexpected error occurred";
        UnexpectedException exception = new UnexpectedException(errorMessage);

        // Act
        ResponseEntity<ApiResponse> response = globalExceptionHandler.handleUnexpected(exception, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertEquals("UNEXPECTED_ERROR", response.getBody().getErrorCode());
        assertEquals(TEST_URI, response.getBody().getPath());
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException")
    void testHandleValidationException() {
        // Arrange
        String errorMessage = "Validation failed";
        FieldError fieldError = new FieldError("objectName", "fieldName", errorMessage);
        
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldError()).thenReturn(fieldError);

        // Act
        ResponseEntity<ApiResponse> response = globalExceptionHandler.handleValidation(methodArgumentNotValidException, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertEquals("VALIDATION_ERROR", response.getBody().getErrorCode());
        assertEquals(TEST_URI, response.getBody().getPath());
    }

    @Test
    @DisplayName("Should handle generic Exception")
    void testHandleGlobalException() {
        // Arrange
        String errorMessage = "Something went wrong";
        Exception exception = new Exception(errorMessage);

        // Act
        ResponseEntity<ApiResponse> response = globalExceptionHandler.handleGlobalException(exception, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().contains("Internal Server Error"));
        assertTrue(response.getBody().getMessage().contains(errorMessage));
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().getErrorCode());
        assertEquals(TEST_URI, response.getBody().getPath());
    }

    @Test
    @DisplayName("Should handle RuntimeException as generic exception")
    void testHandleRuntimeException() {
        // Arrange
        String errorMessage = "Runtime error";
        RuntimeException exception = new RuntimeException(errorMessage);

        // Act
        ResponseEntity<ApiResponse> response = globalExceptionHandler.handleGlobalException(exception, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().contains(errorMessage));
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().getErrorCode());
    }

    @Test
    @DisplayName("Should include correct URI in all error responses")
    void testErrorResponsesIncludeURI() {
        // Test with different URIs
        String customUri = "/api/v1/custom/path";
        when(request.getRequestURI()).thenReturn(customUri);

        ResourceNotFoundException exception = new ResourceNotFoundException("Test error");
        ResponseEntity<ApiResponse> response = globalExceptionHandler.handleResourceNotFound(exception, request);

        assertEquals(customUri, response.getBody().getPath());
    }
}
