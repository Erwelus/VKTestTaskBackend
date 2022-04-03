package com.ervelus.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class TokenSecurityFilterTest {
    static String correctReq;
    static String incorrectReq;
    TokenSecurityFilter filter;
    static TokenProvider provider;

    @BeforeAll
    static void init(){
        correctReq = "token&other";
        incorrectReq = "token other";
        provider = Mockito.mock(TokenProvider.class);
        Mockito.when(provider.validateToken(correctReq)).thenReturn(true);
        Mockito.when(provider.validateToken(incorrectReq)).thenReturn(false);
        Mockito.when(provider.resolveToken(correctReq)).thenReturn(correctReq);
        Mockito.when(provider.resolveToken(incorrectReq)).thenReturn(incorrectReq);
    }

    @BeforeEach
    void setUp() {
        filter = new TokenSecurityFilter();
        filter.setProvider(provider);
    }

    @Test
    void doFilter() {
        Assertions.assertTrue(filter.doFilter(correctReq));
        Assertions.assertFalse(filter.doFilter(incorrectReq));
        Assertions.assertFalse(filter.doFilter(""));
        Assertions.assertFalse(filter.doFilter(null));
    }
}