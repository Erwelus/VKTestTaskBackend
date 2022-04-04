package com.ervelus.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class for units of Friend list
 */
@Data
@NoArgsConstructor
public class FriendListEntry {
    /**
     * Used for DB entries
     */
    private Integer id;
    /**
     * User, who owns the friend list
     */
    private User owner;
    /**
     * Friend of owner
     */
    private User friend;
    /**
     * Status of friend request
     * @see FriendStatus
     */
    private FriendStatus status;

    public FriendListEntry(User owner, User friend, FriendStatus status) {
        this.owner = owner;
        this.friend = friend;
        this.status = status;
    }
}
