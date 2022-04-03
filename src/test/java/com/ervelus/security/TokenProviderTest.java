package com.ervelus.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenProviderTest {
    TokenProvider provider;
    static String secret;
    static String validToken;
    static String invalidToken;
    static String correctUsername;
    static String incorrectUsername;
    static String correctRequest;

    @BeforeAll
    static void init(){
        secret="secret";
        correctUsername="username";
        incorrectUsername="123";
        validToken="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZSJ9.qmaQQC0dRNqj-43IVxsOlziKQMibjSVM6E3_-rDVKWU";
        invalidToken="absfhlfbashdfbhabfasdb";
        correctRequest=validToken+"&"+invalidToken;
    }

    @BeforeEach
    void setUp() {
        provider = new TokenProvider();
        provider.setTokenSecret(secret);
    }

    @Test
    void createToken() {
        Assertions.assertEquals(validToken, provider.createToken(correctUsername));
        Assertions.assertNotEquals(invalidToken, provider.createToken(correctUsername));
        Assertions.assertNotEquals(validToken, provider.createToken(incorrectUsername));
        Assertions.assertNull(provider.createToken(""));
        Assertions.assertNull(provider.createToken(null));
    }

    @Test
    void validateToken() {
        Assertions.assertTrue(provider.validateToken(validToken));
        Assertions.assertFalse(provider.validateToken(invalidToken));
        Assertions.assertFalse(provider.validateToken(null));
        Assertions.assertFalse(provider.validateToken(""));
    }

    @Test
    void getUsernameFromToken() {
        Assertions.assertEquals(correctUsername, provider.getUsernameFromToken(validToken));
        Assertions.assertNull(provider.getUsernameFromToken(invalidToken));
        Assertions.assertNull(provider.getUsernameFromToken(""));
        Assertions.assertNull(provider.getUsernameFromToken(null));
    }

    @Test
    void resolveToken() {
        Assertions.assertEquals(validToken, provider.resolveToken(correctRequest));
        Assertions.assertNotEquals(validToken, provider.resolveToken(""));
        Assertions.assertNotEquals(validToken, provider.resolveToken(null));
    }
}