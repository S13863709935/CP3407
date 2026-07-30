package com.example.exception;

import com.example.common.Result;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final MockHttpServletRequest request = new MockHttpServletRequest();

    @Test
    void unexpectedExceptionReturnsTheStableSystemErrorEnvelope() {
        Result result = handler.error(request, new IllegalStateException("test failure"));

        assertEquals("500", result.getCode());
    }

    @Test
    void customExceptionPreservesItsPublicCodeAndMessage() {
        Result result = handler.customError(request,
                new CustomException("409", "Conflict"));

        assertEquals("409", result.getCode());
        assertEquals("Conflict", result.getMsg());
    }
}
