package com.ervelus.repository;

import com.ervelus.model.User;

public interface UserRepository {
    void save(User user);
    User findUserByUsername(String username);
}
