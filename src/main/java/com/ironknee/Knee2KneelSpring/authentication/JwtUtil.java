package com.ironknee.Knee2KneelSpring.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-validity-in-seconds}")
    private Long expirationTime;

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    public Boolean isTokenValid(String token, String username) {
        final String extractedUsername = extractClaims(token).getSubject();
        return extractedUsername.equals(username) && !isTokenExpired(token);
    }

    private Boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public Claims extractTokenValue(String token) {
        String tokenValue;

        if(StringUtils.hasText(token) && token.startsWith("Bearer "))
            tokenValue = token.substring(7);
        else throw new NullPointerException("Token not found");

        return Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(tokenValue).getBody();
    }

}
