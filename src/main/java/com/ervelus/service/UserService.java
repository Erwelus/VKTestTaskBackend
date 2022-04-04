package com.ervelus.service;

import com.ervelus.model.User;

/**
 * Interface for business-logic related to users
 */
public interface UserService {
    /**
     * Registers new user in system
     */
    void register(User user);

    /**
     * Returns a user by given username. Usernames should be unique
     */
    User findByUsername(String username);

    /**
     * Validates login and password of the user
     * @param username username from request
     * @param password token from request
     * @param user correct user corresponding given username
     * @return true, if login and password are correct
     */
    boolean validateCredentials(String username, String password, User user);
}
