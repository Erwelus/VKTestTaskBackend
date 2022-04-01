package com.ervelus.security;

import com.ervelus.infrastructure.annotations.InjectProperty;
import io.jsonwebtoken.*;

import java.util.Date;

public class TokenProvider {
    @InjectProperty("secret")
    private String tokenSecret;

    public String createToken(String username){
        Date now = new Date();
        Date expired = new Date(now.getTime() + 30*1000*60);
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(expired)
                .signWith(SignatureAlgorithm.HS256, tokenSecret)
                .compact();
    }

    public boolean validateToken(String token){
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(tokenSecret).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        }catch (JwtException | IllegalArgumentException ex){
            return false;
        }
    }

    public String getUsernameFromToken(String token){
        return Jwts.parser().setSigningKey(tokenSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(String request){
        return request.split("&")[0];
    }
}
