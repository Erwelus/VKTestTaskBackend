package com.ervelus.repository;

import com.ervelus.model.FriendListEntry;
import com.ervelus.model.User;

import java.util.List;

/**
 * Interface for persisting friend relations
 */
public interface FriendListRepository {
    /**
     * Saves request into DB
     */
    void saveFriendRequest(FriendListEntry entry);

    /**
     * Updates request in DB. Used when friend accepted request
     */
    void updateFriendRequest(FriendListEntry entry);

    /**
     * Removes request from DB. Used when friend rejected request
     */
    void deleteFriendRequestByBothUsers(User userFrom, User userTo);

    /**
     * Loads friend list of user from DB
     */
    List<FriendListEntry> getFriendList(User user);
}
