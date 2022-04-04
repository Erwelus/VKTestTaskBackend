package com.ervelus.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Entity class of the user
 */
@Data
@NoArgsConstructor
public class User {
    /**
     * Used for DB entries
     */
    private Integer id;
    private String username;
    private String password;
    private List<FriendListEntry> friends;

    /**
     * used by repositories when we get the user from DB
     */
    public User(Integer id, String username, String password){
        this.id = id;
        this.username=username;
        this.password=password;
    }

    /**
     * used by business logic
     */
    public User(String username, String password){
        this.username=username;
        this.password=password;
    }
}
