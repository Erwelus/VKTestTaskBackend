package com.ervelus.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Message {
    private Integer id;
    private User userFrom;
    private User userTo;
    private String content;

    public Message(User userFrom, User userTo, String content){
        this.userFrom=userFrom;
        this.userTo=userTo;
        this.content=content;
    }
}
