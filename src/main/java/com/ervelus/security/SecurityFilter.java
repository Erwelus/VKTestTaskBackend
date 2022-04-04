package com.ervelus.security;

/**
 * Interface for security filters,
 * should be used for filtering incoming requests
 */
public interface SecurityFilter {
    /**
     * Main method of the interface, checks if the request should be processed by the server
     * @param request incoming request
     * @return true if request should be processed, false if not
     */
    boolean doFilter(String request);
}
