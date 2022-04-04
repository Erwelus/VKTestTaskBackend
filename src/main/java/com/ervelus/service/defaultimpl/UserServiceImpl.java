package com.ervelus.service.defaultimpl;

import com.ervelus.infrastructure.annotations.Component;
import com.ervelus.infrastructure.annotations.InjectByType;
import com.ervelus.model.User;
import com.ervelus.repository.UserRepository;
import com.ervelus.service.UserService;
import lombok.Setter;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * Default implementation of UserService.
 * Is annotated with @Component, thus will be saved in context
 */
@Component
public class UserServiceImpl implements UserService {
    /**
     * Instance of UserRepository for persisting users.
     * Setter is only used for tests, is automatically injected
     */
    @InjectByType
    @Setter
    private UserRepository userRepository;

    /**
     * Hashes the password and persists user
     */
    @Override
    public void register(User user) {
        if (user != null) {
            user.setPassword(DigestUtils.sha256Hex(user.getPassword()));
            userRepository.save(user);
        }
    }

    /**
     * Finds user in storage by given username.
     * @return user, if present, null otherwise
     */
    @Override
    public User findByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    /**
     * Checks if given username and password corresponds given user
     * @param username username from request
     * @param password token from request
     * @param user correct user corresponding given username
     * @return true, if username and password are correct and no null values provided
     */
    @Override
    public boolean validateCredentials(String username, String password, User user) {
        if (user == null || username == null || password == null) return false;
        return username.equals(user.getUsername()) && user.getPassword().equals(DigestUtils.sha256Hex(password));
    }
}
