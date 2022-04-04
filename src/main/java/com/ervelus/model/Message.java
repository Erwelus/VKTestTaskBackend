package com.ervelus.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class for unit of chat
 */
@Data
@NoArgsConstructor
public class Message {
    /**
     * Used for DB entries
     */
    private Integer id;
    /**
     * User who sent the message
     */
    private User userFrom;
    /**
     * User who received the message
     */
    private User userTo;
    private String content;

    public Message(User userFrom, User userTo, String content){
        this.userFrom=userFrom;
        this.userTo=userTo;
        this.content=content;
    }
}
