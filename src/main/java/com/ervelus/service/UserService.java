package com.ervelus.service;

import com.ervelus.model.User;

public interface UserService {
    void register(User user);
    User findByUsername(String username);
    void sendFriendRequest(User user, User friend);
    void acceptFriendRequest(User user, User friend);
    void declineFriendRequest(User user, User friend);
}
