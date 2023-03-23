package com.example.javalib;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

public class jwtgenerate {
    public String tokenGenerator() {
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Generate a new secure secret key
        String username = "hello"; // Replace with the user's username
        String password = "world"; // Replace with the user's password
        String subject = username + ":" + password; // Concatenate username and password
        String token = Jwts.builder()
                .setSubject(subject)
                .signWith(key) // Use the generated key for signing
                .compact();
        return token;
    }

    public static void main(String[] args) {
        jwtgenerate generator = new jwtgenerate(); // Create a new instance of jwtgenerate
        String token = generator.tokenGenerator(); // Call the tokenGenerator method on the instance
        System.out.println(token); // Print the generated token to the console
    }
}