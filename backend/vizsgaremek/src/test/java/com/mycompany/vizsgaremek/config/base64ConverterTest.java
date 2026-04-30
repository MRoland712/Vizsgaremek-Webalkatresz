package com.mycompany.vizsgaremek.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class base64ConvertersTest {

    @Test
    void base64Converter_alap64Dekodol() {
        // "hello" base64-ben: aGVsbG8=
        String result = base64Converters.base64Converter("aGVsbG8=");
        assertEquals("hello", result);
    }

    @Test
    void base64Converter_uresStringre_uresSzoveget() {
        String result = base64Converters.base64Converter("");
        assertEquals("", result);
    }

    @Test
    void base64Converter_specKarakter() {
        // "test@123!" base64-ben: dGVzdEAxMjMh
        String result = base64Converters.base64Converter("dGVzdEAxMjMh");
        assertEquals("test@123!", result);
    }
}