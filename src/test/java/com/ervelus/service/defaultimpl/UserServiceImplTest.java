package com.ervelus.service.defaultimpl;

import com.ervelus.model.User;
import com.ervelus.repository.UserRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceImplTest {
    static String username;
    static String not_existing_username;
    static String wrong_password;
    static String password;
    static String encodedPassword;
    static User user;
    static User userWithEncoded;
    static UserRepository userRepository;
    UserServiceImpl service;

    @BeforeAll
    static void init(){
        username = "test_user";
        password = "password";
        wrong_password = "wrong";
        encodedPassword = DigestUtils.sha256Hex(password);
        not_existing_username = "123";
        userRepository = Mockito.mock(UserRepository.class);
        userWithEncoded = new User(1, username, encodedPassword);
        Mockito.when(userRepository.findUserByUsername(username)).thenReturn(userWithEncoded);
        Mockito.when(userRepository.findUserByUsername(not_existing_username)).thenReturn(null);
        Mockito.when(userRepository.findUserByUsername(null)).thenReturn(null);
    }

    @BeforeEach
    void setUp() {
        service = new UserServiceImpl();
        service.setUserRepository(userRepository);
        user = new User(1, username, password);
    }

    @Test
    void register() {
        service.register(user);
        service.register(null);
        Assertions.assertEquals(user, userWithEncoded);
        Assertions.assertEquals(encodedPassword, user.getPassword());
    }

    @Test
    void findByUsername() {
        Assertions.assertEquals(userWithEncoded, service.findByUsername(username));
        Assertions.assertNotEquals(user, service.findByUsername(username));
        Assertions.assertNull(service.findByUsername(not_existing_username));
        Assertions.assertNull(service.findByUsername(null));
    }

    @Test
    void validateCredentials() {
        Assertions.assertTrue(service.validateCredentials(username, password, userWithEncoded));
        Assertions.assertFalse(service.validateCredentials(not_existing_username, password, userWithEncoded));
        Assertions.assertFalse(service.validateCredentials(username, wrong_password, userWithEncoded));
        Assertions.assertFalse(service.validateCredentials(username, password, user));
        Assertions.assertFalse(service.validateCredentials(username, password, null));
    }
}