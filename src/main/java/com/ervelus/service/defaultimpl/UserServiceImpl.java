package com.ervelus.service.defaultimpl;

import com.ervelus.infrastructure.annotations.Component;
import com.ervelus.infrastructure.annotations.InjectByType;
import com.ervelus.model.User;
import com.ervelus.repository.UserRepository;
import com.ervelus.service.UserService;
import lombok.Setter;
import org.apache.commons.codec.digest.DigestUtils;


@Component
public class UserServiceImpl implements UserService {
    @InjectByType
    @Setter
    private UserRepository userRepository;

    @Override
    public void register(User user) {
        if (user != null) {
            user.setPassword(DigestUtils.sha256Hex(user.getPassword()));
            userRepository.save(user);
        }
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public boolean validateCredentials(String username, String password, User user) {
        if (user == null || username == null || password == null) return false;
        return username.equals(user.getUsername()) && user.getPassword().equals(DigestUtils.sha256Hex(password));
    }
}
