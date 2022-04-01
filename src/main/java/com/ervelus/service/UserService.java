package com.ervelus.service;

import com.ervelus.model.FriendListEntry;
import com.ervelus.model.Message;
import com.ervelus.model.User;

import java.util.List;

public interface UserService {
    void register(User user);
    User findByUsername(String username);
    boolean validateCredentials(String username, String password, User user);
}
