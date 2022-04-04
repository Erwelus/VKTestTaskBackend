package com.ervelus.repository;

import com.ervelus.model.User;

/**
 * Interface for persisting Users
 */
public interface UserRepository {
    /**
     * Save user into DB
     */
    void save(User user);

    /**
     * Get user object from DB by his username.
     * Username should be unique
     */
    User findUserByUsername(String username);
}
