package com.ervelus.repository;

import com.ervelus.model.FriendListEntry;
import com.ervelus.model.User;

import java.util.List;

public interface FriendListRepository {
    void saveFriendRequest(FriendListEntry entry);
    void updateFriendRequest(FriendListEntry entry);
    void deleteFriendRequestByBothUsers(User userFrom, User userTo);
    List<FriendListEntry> getFriendList(User user);
}
