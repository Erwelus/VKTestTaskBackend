package com.ervelus.service;

import com.ervelus.model.FriendListEntry;

import java.util.List;

public interface FriendService {
    boolean sendFriendRequest(String username, String friendName);
    boolean acceptFriendRequest(String username, String friendName);
    boolean rejectFriendRequest(String username, String friendName);
    List<String> getFriendList(String username);
    List<String> getFriendNameList(String username);
    List<FriendListEntry> getFriendEntryList(String username);
}
