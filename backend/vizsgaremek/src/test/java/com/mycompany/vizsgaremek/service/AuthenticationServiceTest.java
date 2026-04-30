package com.mycompany.vizsgaremek.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthenticationServiceTest {

    private AuthenticationService.errorAuth errorAuth;
    private AuthenticationService.userAuth userAuth;

    @BeforeEach
    void setUp() {
        errorAuth = new AuthenticationService.errorAuth();
        userAuth = new AuthenticationService.userAuth();
    }

    // ---- errorAuth tesztek ----

    @Test
    void hasErrors_uresTombNincsHiba() {
        JSONArray errors = new JSONArray();
        assertFalse(AuthenticationService.errorAuth.hasErrors(errors));
    }

    @Test
    void hasErrors_telihaTombVanHiba() {
        JSONArray errors = new JSONArray();
        errors.put("MissingEmail");
        assertTrue(AuthenticationService.errorAuth.hasErrors(errors));
    }

    @Test
    void createErrorResponse_400_helyes() {
        JSONArray errors = new JSONArray();
        errors.put("MissingEmail");
        JSONObject result = AuthenticationService.errorAuth.createErrorResponse(errors, 400);

        assertEquals("failed", result.getString("status"));
        assertEquals(400, result.getInt("statusCode"));
        assertTrue(result.getJSONArray("errors").toString().contains("MissingEmail"));
    }

    @Test
    void createErrorResponse_404_helyes() {
        JSONArray errors = new JSONArray();
        errors.put("UserNotFound");
        JSONObject result = AuthenticationService.errorAuth.createErrorResponse(errors, 404);

        assertEquals(404, result.getInt("statusCode"));
    }

    @Test
    void createOKResponse_JSONObject_helyes() {
        JSONObject data = new JSONObject();
        data.put("username", "testUser");
        JSONObject result = AuthenticationService.errorAuth.createOKResponse(data);

        assertEquals("success", result.getString("status"));
        assertEquals(200, result.getInt("statusCode"));
        assertEquals("testUser", result.getJSONObject("result").getString("username"));
    }

    @Test
    void createOKResponse_JSONArray_helyes() {
        JSONArray data = new JSONArray();
        data.put("elem1");
        JSONObject result = AuthenticationService.errorAuth.createOKResponse(data);

        assertEquals("success", result.getString("status"));
        assertEquals(200, result.getInt("statusCode"));
    }

    @Test
    void createOKResponse_parameternelkul_helyes() {
        JSONObject result = AuthenticationService.errorAuth.createOKResponse();
        assertEquals("success", result.getString("status"));
        assertEquals(200, result.getInt("statusCode"));
    }

    // ---- userAuth tesztek ----

    @Test
    void isDataMissing_String_null_igaz() {
        assertTrue(userAuth.isDataMissing((String) null));
    }

    @Test
    void isDataMissing_String_nemNull_hamis() {
        assertFalse(userAuth.isDataMissing("valami"));
    }

    @Test
    void isDataMissing_Integer_null_igaz() {
        assertTrue(userAuth.isDataMissing((Integer) null));
    }

    @Test
    void isDataMissing_Integer_nemNull_hamis() {
        assertFalse(userAuth.isDataMissing(5));
    }

    @Test
    void isValidEmail_helyesEmail_igaz() {
        assertTrue(userAuth.isValidEmail("test@example.com"));
    }

    @Test
    void isValidEmail_rossz_hamis() {
        assertFalse(userAuth.isValidEmail("nemEmail"));
        assertFalse(userAuth.isValidEmail("@nincs.com"));
        assertFalse(userAuth.isValidEmail("nincs@"));
    }

    @Test
    void isValidPassword_heleysJelszo_igaz() {
        // legalább 8 karakter, nagybetű, szám, speciális karakter
        assertTrue(userAuth.isValidPassword("Jelszo1!"));
        assertTrue(userAuth.isValidPassword("TesztJelszo1!"));
    }

    @Test
    void isValidPassword_gyenge_hamis() {
        assertFalse(userAuth.isValidPassword("gyenge"));        // nincs nagybetű/szám/speciális
        assertFalse(userAuth.isValidPassword("Rovid1!"));       // 8 alatti karakter
        assertFalse(userAuth.isValidPassword("nincsnagybetu1!"));
    }

    @Test
    void isValidUsername_helyes_igaz() {
        assertTrue(userAuth.isValidUsername("teszt"));
        assertTrue(userAuth.isValidUsername("abc"));            // minimum 3
        assertTrue(userAuth.isValidUsername("harminc_karakteres_name12")); // 30 alatt
    }

    @Test
    void isValidUsername_rovid_hamis() {
        assertFalse(userAuth.isValidUsername("ab")); // 2 karakter, minimum 3 kell
    }

    @Test
    void isValidId_pozitiv_igaz() {
        assertTrue(userAuth.isValidId(1));
        assertTrue(userAuth.isValidId(9999));
    }

    @Test
    void isValidId_nulla_hamis() {
        assertFalse(userAuth.isValidId(0));
        assertFalse(userAuth.isValidId(-1));
    }

    @Test
    void isValidPhone_rovid_igaz() {
        assertTrue(userAuth.isValidPhone("+36201234567")); // 50 alatt
    }

    @Test
    void isValidRegistrationToken_36kar_igaz() {
        assertTrue(userAuth.isValidRegistrationToken("550e8400-e29b-41d4-a716-446655440000")); // UUID = 36 kar
    }

    @Test
    void isValidRegistrationToken_nemUUID_hamis() {
        assertFalse(userAuth.isValidRegistrationToken("rovid"));
    }

    @Test
    void isUserDeleted_true_igaz() {
        assertTrue(userAuth.isUserDeleted(true));
    }

    @Test
    void isUserDeleted_false_hamis() {
        assertFalse(userAuth.isUserDeleted(false));
    }

    // ---- ordersAuth tesztek ----

    @Test
    void ordersAuth_isValidStatus_helyesStatusz_igaz() {
        AuthenticationService.ordersAuth auth = new AuthenticationService.ordersAuth();
        assertTrue(auth.isValidStatus("delivered"));
        assertTrue(auth.isValidStatus("pending"));
        assertTrue(auth.isValidStatus("inTransit"));
    }

    @Test
    void ordersAuth_isValidStatus_rossz_hamis() {
        AuthenticationService.ordersAuth auth = new AuthenticationService.ordersAuth();
        assertFalse(auth.isValidStatus("shipped"));
        assertFalse(auth.isValidStatus(""));
    }

    @Test
    void ordersAuth_isValidQuantity_pozitiv_igaz() {
        AuthenticationService.ordersAuth auth = new AuthenticationService.ordersAuth();
        assertTrue(auth.isValidQuantity(1));
        assertTrue(auth.isValidQuantity(100));
    }

    @Test
    void ordersAuth_isValidQuantity_nulla_hamis() {
        AuthenticationService.ordersAuth auth = new AuthenticationService.ordersAuth();
        assertFalse(auth.isValidQuantity(0));
        assertFalse(auth.isValidQuantity(-5));
    }

    // ---- paymentsAuth tesztek ----

    @Test
    void paymentsAuth_isValidStatus_helyes_igaz() {
        assertTrue(AuthenticationService.paymentsAuth.isValidStatus("pending"));
        assertTrue(AuthenticationService.paymentsAuth.isValidStatus("completed"));
        assertTrue(AuthenticationService.paymentsAuth.isValidStatus("failed"));
        assertTrue(AuthenticationService.paymentsAuth.isValidStatus("refunded"));
        assertTrue(AuthenticationService.paymentsAuth.isValidStatus("cancelled"));
    }

    @Test
    void paymentsAuth_isValidStatus_rossz_hamis() {
        assertFalse(AuthenticationService.paymentsAuth.isValidStatus("done"));
        assertFalse(AuthenticationService.paymentsAuth.isValidStatus(null));
    }

    @Test
    void paymentsAuth_isValidMethod_helyes_igaz() {
        assertTrue(AuthenticationService.paymentsAuth.isValidMethod("credit_card"));
        assertTrue(AuthenticationService.paymentsAuth.isValidMethod("paypal"));
        assertTrue(AuthenticationService.paymentsAuth.isValidMethod("cash_on_delivery"));
    }

    @Test
    void paymentsAuth_isValidMethod_rossz_hamis() {
        assertFalse(AuthenticationService.paymentsAuth.isValidMethod("bitcoin"));
        assertFalse(AuthenticationService.paymentsAuth.isValidMethod(null));
    }

    // ---- carsAuth tesztek ----

    @Test
    void carsAuth_isValidYearFrom_helyes_igaz() {
        AuthenticationService.carsAuth auth = new AuthenticationService.carsAuth();
        assertTrue(auth.isValidYearFrom(2000));
        assertTrue(auth.isValidYearFrom(1990));
        assertTrue(auth.isValidYearFrom(2035));
    }

    @Test
    void carsAuth_isValidYearFrom_kulfoldHataron_hamis() {
        AuthenticationService.carsAuth auth = new AuthenticationService.carsAuth();
        assertFalse(auth.isValidYearFrom(1989));
        assertFalse(auth.isValidYearFrom(2036));
        assertFalse(auth.isValidYearFrom(null));
    }

    @Test
    void carsAuth_isYearRangeValid_helyesRange_igaz() {
        AuthenticationService.carsAuth auth = new AuthenticationService.carsAuth();
        assertTrue(auth.isYearRangeValid(2000, 2020));
        assertTrue(auth.isYearRangeValid(2010, 2010));  // egyenlő is ok
    }

    @Test
    void carsAuth_isYearRangeValid_fordított_hamis() {
        AuthenticationService.carsAuth auth = new AuthenticationService.carsAuth();
        assertFalse(auth.isYearRangeValid(2025, 2010));
    }

    // ---- partCompatibilityAuth tesztek ----

    @Test
    void partCompatibilityAuth_isValidVehicleType_helyes_igaz() {
        AuthenticationService.partCompatibilityAuth auth = new AuthenticationService.partCompatibilityAuth();
        assertTrue(auth.isValidVehicleType("car"));
        assertTrue(auth.isValidVehicleType("motor"));
        assertTrue(auth.isValidVehicleType("truck"));
    }

    @Test
    void partCompatibilityAuth_isValidVehicleType_rossz_hamis() {
        AuthenticationService.partCompatibilityAuth auth = new AuthenticationService.partCompatibilityAuth();
        assertFalse(auth.isValidVehicleType("bike"));
        assertFalse(auth.isValidVehicleType(null));
    }

    // ---- passwordResetsAuth tesztek ----

    @Test
    void passwordResetsAuth_isValidEmail_helyes_igaz() {
        AuthenticationService.passwordResetsAuth auth = new AuthenticationService.passwordResetsAuth();
        assertTrue(auth.isValidEmail("user@test.com"));
    }

    @Test
    void passwordResetsAuth_isValidEmail_rossz_hamis() {
        AuthenticationService.passwordResetsAuth auth = new AuthenticationService.passwordResetsAuth();
        assertFalse(auth.isValidEmail("nemEmail"));
    }

    @Test
    void passwordResetsAuth_isValidPassword_helyes_igaz() {
        AuthenticationService.passwordResetsAuth auth = new AuthenticationService.passwordResetsAuth();
        assertTrue(auth.isValidPassword("Jelszo1!"));
    }

    // ---- userLogsAuth tesztek ----

    @Test
    void userLogsAuth_isValidAction_helyes_igaz() {
        AuthenticationService.userLogsAuth auth = new AuthenticationService.userLogsAuth();
        assertTrue(auth.isValidAction("login"));      // 3-255 között
        assertTrue(auth.isValidAction("createUser"));
    }

    @Test
    void userLogsAuth_isValidAction_rovid_hamis() {
        AuthenticationService.userLogsAuth auth = new AuthenticationService.userLogsAuth();
        assertFalse(auth.isValidAction("ab"));  // 2 karakter, minimum 3
        assertFalse(auth.isValidAction(null));
    }
}