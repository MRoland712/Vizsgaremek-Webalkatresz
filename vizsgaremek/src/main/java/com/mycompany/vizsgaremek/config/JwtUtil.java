package com.mycompany.vizsgaremek.config;

import com.mycompany.vizsgaremek.service.AuthenticationService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONArray;

/**
 * JWT Token Utility Compatible with JJWT 0.11.x and Java 17
 *
 * @author ddori
 */
public class JwtUtil {

    // Secret key for signing JWT tokens (minimum 32 characters for HS256!)
    private static final String SECRET_KEY = base64Converters.base64Converter(KvFetcher.getDataFromKV("JWTSecretKey"));

    // Token validity: 24 hours 24 * 60 * 60 * 1000
    private static final long TOKEN_VALIDITY = 24 * 60 * 60 * 1000;

    /**
     * Get signing key from SECRET_KEY string
     */
    private static Key getSigningKey() {
        // Ensure the key is at least 256 bits (32 bytes) for HS256
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generate JWT Token
     *
     * @param userId User's ID
     * @param email User's email
     * @param role User's role
     * @param username User's username
     * @return JWT token string
     */
    public static String generateToken(Integer userId, String email, String role, String username) {
        if (role == null) {
            role = "user";
        }

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + TOKEN_VALIDITY);

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userId);
        claims.put("email", email);
        claims.put("role", role);
        claims.put("username", username);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // ← CHANGED: Uses Key object
                .compact();
    }

    /**
     * Validate JWT token
     *
     * @param token JWT token string
     * @return true if valid, false otherwise
     */
    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder() 
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            System.err.println("Invalid JWT token: " + e.getMessage());
            return false;
        }
    }

    /**
     * Extract user ID from token
     *
     * @param token JWT token string
     * @return User ID
     */
    public static Integer extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return (Integer) claims.get("id");  
    }

    /**
     * Extract email from token
     *
     * @param token JWT token string
     * @return Email address
     */
    public static String extractEmail(String token) {
        Claims claims = extractAllClaims(token);
        return (String) claims.get("email");
    }

    /**
     * Extract role from token
     *
     * @param token JWT token string
     * @return User role
     */
    public static String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return (String) claims.get("role");
    }

    /**
     * Extract Username from token
     *
     * @param token JWT token string
     * @return Username
     */
    public static String extractUsername(String token) {
        Claims claims = extractAllClaims(token);
        return (String) claims.get("username");
    }

    /**
     * Extract issued at (iat) timestamp from token
     *
     * @param token JWT token string
     * @return Date when token was issued
     */
    public static Date extractIssuedAt(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getIssuedAt();
    }

    /**
     * Extract expiration (exp) timestamp from token
     *
     * @param token JWT token string
     * @return Date when token expires
     */
    public static Date extractExpiration(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getExpiration();
    }

    /**
     * Check if token is expired
     *
     * @param token JWT token string
     * @return true if expired, false otherwise
     */
    public static boolean isTokenExpired(String token) {
        try {
            Date expiration = extractAllClaims(token).getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Extract all claims from token
     *
     * @param token JWT token string
     * @return Claims object
     */
    private static Claims extractAllClaims(String token) {
        return Jwts.parserBuilder() // ← CHANGED: parserBuilder() instead of parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    AuthenticationService.errorAuth errorAuth = new AuthenticationService.errorAuth();
    public Response validateJwtAndReturnError(String jwtToken) {
        JSONArray errors = new JSONArray();

        if (AuthenticationService.isDataMissing(jwtToken)) {
            errors.put("MissingToken");
            return Response.status(401)
                    .entity(errorAuth.createErrorResponse(errors, 401).toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        Boolean validJwt = validateToken(jwtToken);
        Boolean expiredToken = isTokenExpired(jwtToken);

        if (expiredToken) {
            errors.put("TokenExpired");
            return Response.status(401)
                    .entity(errorAuth.createErrorResponse(errors, 401).toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        if (validJwt == false) {
            errors.put("InvalidToken");
            return Response.status(401)
                    .entity(errorAuth.createErrorResponse(errors, 401).toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        return null; // Token valid
    }
    /**
     * Example usage
     */
    public static void main(String[] args) {
        System.out.println("=== JWT Token Test ===\n");

        // Generate token
        String token = generateToken(12, "test@example.com", null, "testUsername");
        System.out.println("Token: " + token);
        System.out.println("\nValid: " + validateToken(token));
        System.out.println("User ID: " + extractUserId(token));
        System.out.println("Email: " + extractEmail(token));
        System.out.println("Role: " + extractRole(token));
        System.out.println("Username: " + extractUsername(token));
        System.out.println("Issued At: " + extractIssuedAt(token));
        System.out.println("Expiration: " + extractExpiration(token));
        System.out.println("Expired: " + isTokenExpired(token));
    }
}
