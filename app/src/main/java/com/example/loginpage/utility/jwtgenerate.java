package com.example.loginpage.utility;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

public class jwtgenerate {
    public String tokenGenerator(String username, String password) {
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Generate a new secure secret key
        String subject = username + ":" + password; // Concatenate username and password
        String token = Jwts.builder()
                .setSubject(subject)
                .signWith(key) // Use the generated key for signing
                .compact();
        return token;
    }

}