package ar.training.reactive.infrastructure.adapter.in.rest;

import org.junit.jupiter.api.Test;

import static ar.training.reactive.infrastructure.adapter.in.rest.ExceptionsHandler.ExceptionHandlerUtils.nullToLiteralString;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ExceptionsHandlerUtilsTest {

    @Test
    void returnsLiteralNullStringWhenValueIsNull() {
        assertEquals("null", nullToLiteralString(null));
    }

    @Test
    void returnsValueUnchangedWhenValueIsNotNull() {
        assertEquals(Integer.valueOf(5), nullToLiteralString(Integer.valueOf(5)));
    }
}
