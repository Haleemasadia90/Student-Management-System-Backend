package com.mfsys.crud.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {


    private final String SECRET_KEY_STRING = "yeh_ek_bohot_lambi_aur_random_secret_key_honi_chahiye_kam_se_kam_32_characters";

    private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes());

    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 8;


    public String generateToken(String email, String role) {
        return Jwts.builder()
                .subject(email)                         // token "kiske liye" hai
                .claim("role", role)                     // extra data — role bhi daal diya
                .issuedAt(new Date())                    // kab bana
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))  // kab expire hoga
                .signWith(SECRET_KEY)                    // secret key se sign kiya
                .compact();                               // final string bana di
    }


    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }


    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }


    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);   // agar signature/expiry galat hai, ye khud exception dega
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}