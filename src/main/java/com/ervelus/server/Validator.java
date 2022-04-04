package com.ervelus.server;

/**
 * Interface for validating incoming requests
 */
public interface Validator {
    /**
     * Main method of the interface
     * @param request incoming request
     * @param command command that this request contains
     * @return true if request valid, else false
     */
    boolean validate(String request, String command);
}
