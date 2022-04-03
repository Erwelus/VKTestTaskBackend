package com.ervelus.server.request;

import com.ervelus.server.request.CommandResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CommandResolverTest {
    CommandResolver resolver;

    @BeforeEach
    void setUp(){
        resolver = new CommandResolver();
    }

    @Test
    void resolveLogin() {
        Assertions.assertEquals("login", resolver.resolve("login&args"));
        Assertions.assertNotEquals("login", resolver.resolve("loginargs"));
        Assertions.assertNotEquals("login", resolver.resolve("logina&rgs"));
        Assertions.assertEquals("login", resolver.resolve("LOGin&args"));
    }

    @Test
    void resolveRegister() {
        Assertions.assertEquals("register", resolver.resolve("register&args"));
        Assertions.assertNotEquals("register", resolver.resolve("registerargs"));
        Assertions.assertNotEquals("register", resolver.resolve("registera&rgs"));
        Assertions.assertEquals("register", resolver.resolve("REGISTEr&args"));
    }

    @Test
    void resolveExit() {
        Assertions.assertEquals("exit", resolver.resolve("exit&args"));
        Assertions.assertNotEquals("exit", resolver.resolve("exitargs"));
        Assertions.assertNotEquals("exit", resolver.resolve("exita&rgs"));
        Assertions.assertEquals("exit", resolver.resolve("Exit&args"));
    }

    @Test
    void resolveFriends() {
        Assertions.assertEquals("friends", resolver.resolve("token&friends"));
        Assertions.assertNotEquals("friends", resolver.resolve("token&friens"));
        Assertions.assertNotEquals("friends", resolver.resolve("friends&token"));
    }

    @Test
    void resolveAdd() {
        Assertions.assertEquals("add", resolver.resolve("token&add"));
        Assertions.assertNotEquals("add", resolver.resolve("token&adds"));
        Assertions.assertNotEquals("add", resolver.resolve("add&token"));
    }

    @Test
    void resolveAccept() {
        Assertions.assertEquals("accept", resolver.resolve("token&accept"));
        Assertions.assertNotEquals("accept", resolver.resolve("token&accepts"));
        Assertions.assertNotEquals("accept", resolver.resolve("accept&token"));
    }

    @Test
    void resolveReject() {
        Assertions.assertEquals("reject", resolver.resolve("token&reject"));
        Assertions.assertNotEquals("reject", resolver.resolve("token&rejects"));
        Assertions.assertNotEquals("reject", resolver.resolve("reject&token"));
    }

    @Test
    void resolveChat() {
        Assertions.assertEquals("chat", resolver.resolve("token&chat"));
        Assertions.assertNotEquals("chat", resolver.resolve("token&chats"));
        Assertions.assertNotEquals("chat", resolver.resolve("chat&token"));
    }

    @Test
    void resolveSend() {
        Assertions.assertEquals("send", resolver.resolve("token&send"));
        Assertions.assertNotEquals("send", resolver.resolve("token&sends"));
        Assertions.assertNotEquals("send", resolver.resolve("send&token"));
    }
}