package com.ervelus.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class User {
    private Integer id;
    private String username;
    private String password;
    private List<FriendListEntry> friends;

    public User(Integer id, String username, String password){
        this.id = id;
        this.username=username;
        this.password=password;
    }

    public User(String username, String password){
        this.username=username;
        this.password=password;
    }
}
