package com.ervelus.security;

import com.ervelus.infrastructure.annotations.Component;
import com.ervelus.infrastructure.annotations.InjectByType;
import lombok.Setter;

@Component
public class TokenSecurityFilter implements SecurityFilter{
    @InjectByType
    @Setter
    private TokenProvider provider;
    @Override
    public boolean doFilter(String request) {
        return provider.validateToken(provider.resolveToken(request));
    }
}
