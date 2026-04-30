package com.mycompany.vizsgaremek.config;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class EncryptTest {

    // AES kulcs: pontosan 16, 24 vagy 32 bájt kell
    private static final String TEST_KEY = "TesztAesKulcs123"; // 16 karakter

    @BeforeAll
    static void setUpAll() {
        Encrypt.testAesKey = TEST_KEY;
    }

    @AfterAll
    static void tearDownAll() {
        Encrypt.testAesKey = null;
    }

    @Test
    void encrypt_nemNullEredmeny() throws Exception {
        String result = Encrypt.encrypt("tesztJelszo");
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void encrypt_decrypt_azonosEredmeny() throws Exception {
        String plain = "Jelszo123!";
        String encrypted = Encrypt.encrypt(plain);
        String decrypted = Encrypt.decrypt(encrypted);
        assertEquals(plain, decrypted);
    }

    @Test
    void encrypt_kulonbozoInput_kulonbozoOutput() throws Exception {
        String e1 = Encrypt.encrypt("Jelszo1!");
        String e2 = Encrypt.encrypt("Jelszo2!");
        assertNotEquals(e1, e2);
    }

    @Test
    void encrypt_magyarKarakterek() throws Exception {
        String plain = "árvíztűrő_tükörfúrógép";
        String encrypted = Encrypt.encrypt(plain);
        String decrypted = Encrypt.decrypt(encrypted);
        assertEquals(plain, decrypted);
    }

    @Test
    void encrypt_uresSzoveg() throws Exception {
        String encrypted = Encrypt.encrypt("");
        String decrypted = Encrypt.decrypt(encrypted);
        assertEquals("", decrypted);
    }

    @Test
    void decrypt_rossz_exceptiont_dob() {
        assertThrows(Exception.class, () -> Encrypt.decrypt("nemBase64!@#$"));
    }
}