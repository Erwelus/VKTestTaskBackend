package com.ervelus.server.request;

import com.ervelus.server.request.RequestValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RequestValidatorTest {
    RequestValidator validator;

    @BeforeEach
    void setUp() {
        validator = new RequestValidator();
    }

    @Test
    void validateLogin() {
        Assertions.assertTrue(validator.validate("login&username&password", "login"));
        Assertions.assertFalse(validator.validate("login&username&password&anything", "login"));
        Assertions.assertFalse(validator.validate("login&username&", "login"));
        Assertions.assertFalse(validator.validate("login&username", "login"));
        Assertions.assertFalse(validator.validate("username&login&password", "login"));
    }

    @Test
    void validateRegister() {
        Assertions.assertTrue(validator.validate("register&username&password", "register"));
        Assertions.assertFalse(validator.validate("register&username&password&anything", "register"));
        Assertions.assertFalse(validator.validate("register&username&", "register"));
        Assertions.assertFalse(validator.validate("register&username", "register"));
        Assertions.assertFalse(validator.validate("username&register&password", "register"));
    }

    @Test
    void validateExit() {
        Assertions.assertTrue(validator.validate("token&exit", "exit"));
        Assertions.assertFalse(validator.validate("token&exit&anything", "exit"));
        Assertions.assertFalse(validator.validate("exit&token", "exit"));
    }

    @Test
    void validateFriends() {
        Assertions.assertTrue(validator.validate("token&friends", "friends"));
        Assertions.assertFalse(validator.validate("token&friends&anything", "friends"));
        Assertions.assertFalse(validator.validate("friends&token", "friends"));
    }

    @Test
    void validateAdd() {
        Assertions.assertTrue(validator.validate("token&add&friend", "add"));
        Assertions.assertFalse(validator.validate("token&add&friend&anything", "add"));
        Assertions.assertFalse(validator.validate("token&add&", "add"));
        Assertions.assertFalse(validator.validate("token&add", "add"));
        Assertions.assertFalse(validator.validate("add&token&friend", "add"));
    }

    @Test
    void validateAccept() {
        Assertions.assertTrue(validator.validate("token&accept&friend", "accept"));
        Assertions.assertFalse(validator.validate("token&accept&friend&anything", "accept"));
        Assertions.assertFalse(validator.validate("token&accept&", "accept"));
        Assertions.assertFalse(validator.validate("token&accept", "accept"));
        Assertions.assertFalse(validator.validate("accept&token&friend", "accept"));
    }

    @Test
    void validateReject() {
        Assertions.assertTrue(validator.validate("token&reject&friend", "reject"));
        Assertions.assertFalse(validator.validate("token&reject&friend&anything", "reject"));
        Assertions.assertFalse(validator.validate("token&reject&", "reject"));
        Assertions.assertFalse(validator.validate("token&reject", "reject"));
        Assertions.assertFalse(validator.validate("reject&token&friend", "reject"));
    }

    @Test
    void validateChat() {
        Assertions.assertTrue(validator.validate("token&chat&friend", "chat"));
        Assertions.assertFalse(validator.validate("token&chat&friend&anything", "chat"));
        Assertions.assertFalse(validator.validate("token&chat&", "chat"));
        Assertions.assertFalse(validator.validate("token&chat", "chat"));
        Assertions.assertFalse(validator.validate("chat&token&friend", "chat"));
    }

    @Test
    void validateSave() {
        Assertions.assertTrue(validator.validate("token&send&friend&text", "send"));
        Assertions.assertFalse(validator.validate("token&send&friend&text&anything", "send"));
        Assertions.assertFalse(validator.validate("token&send&text&", "send"));
        Assertions.assertFalse(validator.validate("token&send&text", "send"));
        Assertions.assertFalse(validator.validate("send&token&friend&text", "send"));
    }
}