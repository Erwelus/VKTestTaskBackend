package com.ervelus.security;

import com.ervelus.infrastructure.annotations.Component;
import com.ervelus.infrastructure.annotations.InjectByType;

@Component
public class TokenSecurityFilter implements SecurityFilter{
    @InjectByType
    private TokenProvider provider;
    @Override
    public boolean doFilter(String request) {
        return provider.validateToken(provider.resolveToken(request));
    }
}
