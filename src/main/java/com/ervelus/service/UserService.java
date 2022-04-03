package com.ervelus.service;

import com.ervelus.model.User;


public interface UserService {
    void register(User user);
    User findByUsername(String username);
    boolean validateCredentials(String username, String password, User user);
}
