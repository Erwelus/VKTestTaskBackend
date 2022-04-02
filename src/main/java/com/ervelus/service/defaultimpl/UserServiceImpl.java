package com.ervelus.service.defaultimpl;

import com.ervelus.infrastructure.annotations.Component;
import com.ervelus.infrastructure.annotations.InjectByType;
import com.ervelus.model.FriendListEntry;
import com.ervelus.model.FriendStatus;
import com.ervelus.model.Message;
import com.ervelus.model.User;
import com.ervelus.repository.FriendListRepository;
import com.ervelus.repository.UserRepository;
import com.ervelus.service.MessageService;
import com.ervelus.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserServiceImpl implements UserService {
    @InjectByType
    private UserRepository userRepository;
    @InjectByType
    private FriendListRepository friendListRepository;

    @Override
    public void register(User user) {
        user.setPassword(DigestUtils.sha256Hex(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public User findFriendByUsername(User owner, String username) {
        return null;
    }

    @Override
    public boolean validateCredentials(String username, String password, User user) {
        return username.equals(user.getUsername()) && user.getPassword().equals(DigestUtils.sha256Hex(password));
    }
}
