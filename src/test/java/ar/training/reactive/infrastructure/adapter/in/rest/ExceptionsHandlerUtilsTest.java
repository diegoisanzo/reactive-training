package ar.training.reactive.infrastructure.adapter.in.rest;

import org.junit.jupiter.api.Test;

import static ar.training.reactive.infrastructure.adapter.in.rest.ExceptionsHandler.ExceptionHandlerUtils.nullToLiteralString;
import static java.lang.Integer.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ExceptionsHandlerUtilsTest {

    @Test
    void returnsLiteralNullStringWhenValueIsNull() {
        assertEquals("null", nullToLiteralString(null));
    }

    @Test
    void returnsValueUnchangedWhenValueIsNotNull() {
        var _5 = valueOf(5);
        assertEquals(_5, nullToLiteralString(_5));
    }
}
