package com.ervelus.service;

import com.ervelus.model.FriendListEntry;

import java.util.List;
/**
 * Interface for business-logic related to friend managing
 */
public interface FriendService {
    /**
     * sends new friend request
     * @param username who sent the request
     * @param friendName who should receive the request
     * @return if the request is sent
     */
    boolean sendFriendRequest(String username, String friendName);

    /**
     * accepts incoming friend request
     * @param username who accepted the request
     * @param friendName who sent the request
     * @return if the request is accepted
     */
    boolean acceptFriendRequest(String username, String friendName);

    /**
     * rejects incoming friend request
     * @param username who rejected the request
     * @param friendName who sent the request
     * @return if the request is rejected
     */
    boolean rejectFriendRequest(String username, String friendName);

    /**
     * Get the list of friend requests as (Friend: Status)
     * @param username owner of friend list
     */
    List<String> getFriendList(String username);

    /**
     * Get the list of usernames in friend list
     * @param username owner of friend list
     */
    List<String> getFriendNameList(String username);

    /**
     * Get the list of friend requests as entities
     * @param username owner of friend list
     */
    List<FriendListEntry> getFriendEntryList(String username);
}
