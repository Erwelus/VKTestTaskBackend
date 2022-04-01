package com.ervelus.service;

import com.ervelus.model.FriendListEntry;
import com.ervelus.model.User;

import java.util.List;

public interface FriendService {
    boolean sendFriendRequest(String username, String friendName);
    boolean acceptFriendRequest(String username, String friendName);
    boolean rejectFriendRequest(String username, String friendName);
    List<String> getFriendNamesList(String username);
    List<FriendListEntry> getFriendList(String username);
}
