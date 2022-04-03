package com.ervelus.security;

import com.ervelus.infrastructure.annotations.InjectProperty;
import io.jsonwebtoken.*;
import lombok.Setter;

import java.util.Date;

public class TokenProvider {
    @InjectProperty("secret")
    @Setter
    private String tokenSecret;

    public String createToken(String username){
        if (username==null || username.isEmpty()) return null;
        return Jwts.builder()
                .setSubject(username)
                .signWith(SignatureAlgorithm.HS256, tokenSecret)
                .compact();
    }

    public boolean validateToken(String token){
        try {
            Jwts.parser().setSigningKey(tokenSecret).parseClaimsJws(token);
            return true;
        }catch (JwtException | IllegalArgumentException ex){
            return false;
        }
    }

    public String getUsernameFromToken(String token){
        if (validateToken(token)) {
            return Jwts.parser().setSigningKey(tokenSecret).parseClaimsJws(token).getBody().getSubject();
        }else return null;
    }

    public String resolveToken(String request){
        if (request==null) return null;
        return request.split("&")[0];
    }
}
