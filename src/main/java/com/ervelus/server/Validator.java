package com.ervelus.server;

public interface Validator {
    boolean validate(String req, String command);
}
