package com.ervelus.model;

import lombok.Data;

@Data
public class Message {
    private Integer id;
    private User userFrom;
    private User userTo;
    private String content;
}
