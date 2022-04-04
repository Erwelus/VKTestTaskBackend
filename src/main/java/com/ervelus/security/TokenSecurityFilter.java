package com.ervelus.security;

import com.ervelus.infrastructure.annotations.Component;
import com.ervelus.infrastructure.annotations.InjectByType;
import lombok.Setter;

/**
 * Security filter that checks if incoming requests use valid token.
 * Is annotated with @Component, used for injection into RequestDispatcher
 */
@Component
public class TokenSecurityFilter implements SecurityFilter{
    /**
     * Utils for working with token.
     * Setter is used only for testing, is injected automatically
     */
    @InjectByType
    @Setter
    private TokenProvider provider;

    /**
     * Checks if token is present and if it is valid
     * @param request incoming request
     */
    @Override
    public boolean doFilter(String request) {
        return provider.validateToken(provider.resolveToken(request));
    }
}
