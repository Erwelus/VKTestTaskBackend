package com.ervelus.controllers;

import com.ervelus.model.User;
import com.ervelus.security.TokenProvider;
import com.ervelus.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class AuthenticationControllerTest {
    AuthenticationController controller;
    static TokenProvider tokenProvider;
    static UserService userService;
    static Map<String, BufferedWriter> emptyMap;
    static String correctUsername;
    static String correctPassword;
    static User correctUser;
    static String incorrectUsername;
    static String incorrectPassword;
    static String token;

    @BeforeAll
    static void init(){
        correctUsername = "username";
        correctPassword = "password";
        incorrectUsername = "asd";
        incorrectPassword = "asd";
        emptyMap = new HashMap<>();
        tokenProvider = Mockito.mock(TokenProvider.class);
        token = "token";
        correctUser = new User(correctUsername, correctPassword);
        Mockito.when(tokenProvider.createToken(correctUsername)).thenReturn(token);
        Mockito.when(tokenProvider.getUsernameFromToken(any())).thenReturn(correctUsername);
        Mockito.when(tokenProvider.validateToken(token)).thenReturn(true);
        userService = Mockito.mock(UserService.class);
        Mockito.when(userService.validateCredentials(correctUsername, correctPassword, correctUser)).thenReturn(true);
        Mockito.when(userService.validateCredentials(eq(incorrectUsername), eq(correctPassword), any())).thenReturn(false);
        Mockito.when(userService.validateCredentials(eq(correctUsername), eq(incorrectPassword), any())).thenReturn(false);
        Mockito.when(userService.findByUsername(correctUsername)).thenReturn(correctUser);
    }

    @BeforeEach
    void setUp() {
        controller = new AuthenticationController();
        controller.setUserService(userService);
        controller.setTokenProvider(tokenProvider);
    }

    @Test
    void loginCorrect() {
        StringWriter stringWriter = new StringWriter();
        BufferedWriter out = new BufferedWriter(stringWriter);
        Map<String, BufferedWriter> mapToInsert = new HashMap<>();
        String expected = "Token: "+token+System.lineSeparator();
        controller.login("login&"+correctUsername+"&"+correctPassword, out, mapToInsert);
        Assertions.assertEquals(expected, stringWriter.toString());
        Assertions.assertNotEquals(emptyMap, mapToInsert);
    }

    @Test
    void loginIncorrectUsername() {
        StringWriter stringWriter = new StringWriter();
        BufferedWriter out = new BufferedWriter(stringWriter);
        Map<String, BufferedWriter> mapToInsert = new HashMap<>();
        String expected = "User not found"+System.lineSeparator();
        controller.login("login&"+incorrectUsername+"&"+correctPassword, out, mapToInsert);
        Assertions.assertEquals(expected, stringWriter.toString());
        Assertions.assertEquals(emptyMap, mapToInsert);
    }

    @Test
    void loginIncorrectPassword() {
        StringWriter stringWriter = new StringWriter();
        BufferedWriter out = new BufferedWriter(stringWriter);
        Map<String, BufferedWriter> mapToInsert = new HashMap<>();
        String expected = "Wrong password"+System.lineSeparator();
        controller.login("login&"+correctUsername+"&"+incorrectPassword, out, mapToInsert);
        Assertions.assertEquals(expected, stringWriter.toString());
        Assertions.assertEquals(emptyMap, mapToInsert);
    }
    @Test
    void loginIncorrectRequest() {
        StringWriter stringWriter = new StringWriter();
        BufferedWriter out = new BufferedWriter(stringWriter);
        Map<String, BufferedWriter> mapToInsert = new HashMap<>();
        controller.login("login"+correctUsername+incorrectPassword, out, mapToInsert);
        Assertions.assertEquals(emptyMap, mapToInsert);
    }

    @Test
    void registerCorrect() {
        StringWriter stringWriter = new StringWriter();
        BufferedWriter out = new BufferedWriter(stringWriter);
        Map<String, BufferedWriter> mapToInsert = new HashMap<>();
        String expected = "Token: "+token+System.lineSeparator();
        controller.login("register&"+correctUsername+"&"+correctPassword, out, mapToInsert);
        Assertions.assertEquals(expected, stringWriter.toString());
        Assertions.assertNotEquals(emptyMap, mapToInsert);
    }

    @Test
    void registerIncorrectRequest() {
        StringWriter stringWriter = new StringWriter();
        BufferedWriter out = new BufferedWriter(stringWriter);
        Map<String, BufferedWriter> mapToInsert = new HashMap<>();
        controller.login("register"+correctUsername+incorrectPassword, out, mapToInsert);
        Assertions.assertEquals(emptyMap, mapToInsert);
    }

    @Test
    void exit() {
        Map<String, BufferedWriter> mapToDelete = new HashMap<>();
        mapToDelete.put(correctUsername, null);
        controller.exit(token+"&exit", mapToDelete);
        Assertions.assertEquals(emptyMap, mapToDelete);
    }
}