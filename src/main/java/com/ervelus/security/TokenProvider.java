package com.ervelus.security;

import com.ervelus.infrastructure.annotations.Component;
import com.ervelus.infrastructure.annotations.InjectProperty;
import io.jsonwebtoken.*;
import lombok.Setter;

import java.util.Date;

/**
 * Class, providing some operations with tokens for authentication.
 * Is annotated with @Component, thus will be saved in context
 */
@Component
public class TokenProvider {
    /**
     * Signing key for tokens.
     * Should pe specified in property file.
     * Setter is used only for tests.
     */
    @InjectProperty("secret")
    @Setter
    private String tokenSecret;

    /**
     * Creates a token based on given username.
     * A token is unique for each user due to unique usernames.
     * @param username username of the user requesting for token
     * @return token, if username is not null and not empty, null otherwise
     */
    public String createToken(String username){
        if (username==null || username.isEmpty()) return null;
        return Jwts.builder()
                .setSubject(username)
                .signWith(SignatureAlgorithm.HS256, tokenSecret)
                .compact();
    }

    /**
     * Validates given token.
     * @param token token to validate
     * @return true, if token is valid, else false
     */
    public boolean validateToken(String token){
        try {
            Jwts.parser().setSigningKey(tokenSecret).parseClaimsJws(token);
            return true;
        }catch (JwtException | IllegalArgumentException ex){
            return false;
        }
    }

    /**
     * @param token Incoming token
     * @return Username, based on which token was created
     */
    public String getUsernameFromToken(String token){
        if (validateToken(token)) {
            return Jwts.parser().setSigningKey(tokenSecret).parseClaimsJws(token).getBody().getSubject();
        }else return null;
    }

    /**
     * Resolves token from request
     * @param request incoming request
     * @return token if request is not null, else null
     */
    public String resolveToken(String request){
        if (request==null) return null;
        return request.split("&")[0];
    }
}
