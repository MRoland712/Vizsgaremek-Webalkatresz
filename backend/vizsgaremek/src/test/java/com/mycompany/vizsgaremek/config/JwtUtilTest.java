package com.mycompany.vizsgaremek.config;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    // 32+ karakter kell a HS256-hoz
    private static final String TEST_KEY = "TesztKulcs1234567890AbcdefghijklMNO";

    @BeforeAll
    static void setUpAll() {
        // A statikus KvFetcher hívást elkerüljük a testSecretKey-jel
        JwtUtil.testSecretKey = TEST_KEY;
    }

    @AfterAll
    static void tearDownAll() {
        JwtUtil.testSecretKey = null;
    }

    @Test
    void generateToken_visszaadStringet() {
        String token = JwtUtil.generateToken(1, "test@test.com", "user", "testuser");
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void validateToken_ervenyes_igaz() {
        String token = JwtUtil.generateToken(1, "test@test.com", "user", "testuser");
        assertTrue(JwtUtil.validateToken(token));
    }

    @Test
    void validateToken_hamis_hamis() {
        assertFalse(JwtUtil.validateToken("nemTokenString.abc.def"));
    }

    @Test
    void extractUserId_helyesId() {
        String token = JwtUtil.generateToken(42, "test@test.com", "user", "testuser");
        assertEquals(42, JwtUtil.extractUserId(token));
    }

    @Test
    void extractEmail_helyesEmail() {
        String token = JwtUtil.generateToken(1, "valaki@pelda.hu", "user", "testuser");
        assertEquals("valaki@pelda.hu", JwtUtil.extractEmail(token));
    }

    @Test
    void extractRole_nullRole_defaultUser() {
        String token = JwtUtil.generateToken(1, "test@test.com", null, "testuser");
        assertEquals("user", JwtUtil.extractRole(token));
    }

    @Test
    void extractRole_admin() {
        String token = JwtUtil.generateToken(1, "admin@test.com", "admin", "adminuser");
        assertEquals("admin", JwtUtil.extractRole(token));
    }

    @Test
    void extractUsername_helyes() {
        String token = JwtUtil.generateToken(1, "test@test.com", "user", "jozsi123");
        assertEquals("jozsi123", JwtUtil.extractUsername(token));
    }

    @Test
    void isTokenExpired_ujToken_hamis() {
        String token = JwtUtil.generateToken(1, "test@test.com", "user", "testuser");
        assertFalse(JwtUtil.isTokenExpired(token));
    }

    @Test
    void isTokenExpired_ervenytelen_igaz() {
        assertTrue(JwtUtil.isTokenExpired("lejartTokenString.abc.def"));
    }

    @Test
    void createSessionToken_nincsDuplika() {
        String t1 = JwtUtil.createSessionToken();
        String t2 = JwtUtil.createSessionToken();
        assertNotEquals(t1, t2);
        assertEquals(36, t1.length()); // UUID formátum
    }

    @Test
    void createSessionTokenWithExpiry_tartalmazzaTokentEsLejarast() {
        var result = JwtUtil.createSessionTokenWithExpiry(60 * 60 * 1000L);
        assertNotNull(result.get("token"));
        assertNotNull(result.get("expiresAt"));
    }

    @Test
    void createSessionToken24h_helyesEredmeny() {
        var result = JwtUtil.createSessionToken24h();
        assertNotNull(result.get("token"));
        assertNotNull(result.get("expiresAt"));
    }

    @Test
    void createSessionToken1h_helyesEredmeny() {
        var result = JwtUtil.createSessionToken1h();
        assertNotNull(result.get("token"));
        assertNotNull(result.get("expiresAt"));
    }
}