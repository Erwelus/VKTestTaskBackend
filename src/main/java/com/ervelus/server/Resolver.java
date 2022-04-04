package com.ervelus.server;

/**
 * Interface for resolving specific string in input
 */
public interface Resolver {
    /**
     * Main method of the interface
     * @param input input string, containing what should be resolved
     * @return resolved String
     */
    String resolve(String input);
}
