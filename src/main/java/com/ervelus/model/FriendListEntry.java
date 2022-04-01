package com.ervelus.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FriendListEntry {
    private Integer id;
    private User owner;
    private User friend;
    private FriendStatus status;

    public FriendListEntry(User owner, User friend, FriendStatus status) {
        this.owner = owner;
        this.friend = friend;
        this.status = status;
    }
}
