package com.ervelus.service.defaultimpl;

import com.ervelus.infrastructure.annotations.InjectByType;
import com.ervelus.model.FriendListEntry;
import com.ervelus.model.FriendStatus;
import com.ervelus.model.User;
import com.ervelus.repository.FriendListRepository;
import com.ervelus.service.FriendService;
import com.ervelus.service.UserService;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of UserService.
 * Is annotated with @Component, thus will be saved in context
 */
public class FriendServiceImpl implements FriendService {
    /**
     * Instance of FriendListRepository for persisting friend requests.
     * Setter is only used for tests, is automatically injected
     */
    @InjectByType
    @Setter
    private FriendListRepository friendListRepository;
    /**
     * Instance of UserService for getting users by their usernames.
     * Setter is only used for tests, is automatically injected
     */
    @InjectByType
    @Setter
    private UserService userService;

    /**
     * Sends friend request, if users are not already friends and none of the args is null
     * @param username who sent the request
     * @param friendName who should receive the request
     */
    @Override
    public boolean sendFriendRequest(String username, String friendName) {
        User user = userService.findByUsername(username);
        User friend = userService.findByUsername(friendName);
        List<String> friendNameList = getFriendNameList(username);
        if (user!=null && friend !=null && !friendNameList.contains(friendName)) {
            FriendListEntry waitingEntry = new FriendListEntry(user, friend, FriendStatus.WAITING);
            FriendListEntry incomingEntry = new FriendListEntry(friend, user, FriendStatus.INCOMING);
            friendListRepository.saveFriendRequest(waitingEntry);
            friendListRepository.saveFriendRequest(incomingEntry);
            return true;
        } else return false;

    }

    /**
     * Accepts incoming friend request, if there is one and none of the args is null
     * @param username who accepted the request
     * @param friendName who sent the request
     */
    @Override
    public boolean acceptFriendRequest(String username, String friendName) {
        User user = userService.findByUsername(username);
        User friend = userService.findByUsername(friendName);
        List<FriendListEntry> friendEntryList = getFriendEntryList(username);
        List<String> friendNameList = getNameListFromEntryList(friendEntryList);
        if (user!=null && friend !=null &&
                friendNameList.contains(friendName) &&
                friendEntryList.get(friendNameList.indexOf(friendName)).getStatus().equals(FriendStatus.INCOMING)) {
            FriendListEntry incomingEntry = new FriendListEntry(user, friend, FriendStatus.ACCEPTED);
            FriendListEntry waitingEntry = new FriendListEntry(friend, user, FriendStatus.ACCEPTED);
            friendListRepository.updateFriendRequest(incomingEntry);
            friendListRepository.updateFriendRequest(waitingEntry);
            return true;
        }else return false;
    }

    /**
     * Rejects incoming friend request, if there is one and none of the args is null
     * @param username who rejected the request
     * @param friendName who sent the request
     * @return
     */
    @Override
    public boolean rejectFriendRequest(String username, String friendName) {
        User user = userService.findByUsername(username);
        User friend = userService.findByUsername(friendName);
        List<FriendListEntry> friendEntryList = getFriendEntryList(username);
        List<String> friendNameList = getNameListFromEntryList(friendEntryList);
        if (user!=null && friend !=null && friendNameList.contains(friendName) &&
                friendEntryList.get(friendNameList.indexOf(friendName)).getStatus().equals(FriendStatus.INCOMING)) {
            friendListRepository.deleteFriendRequestByBothUsers(user, friend);
            friendListRepository.deleteFriendRequestByBothUsers(friend, user);
            return true;
        }else return false;
    }

    /**
     * Returns friend list of given user as (Friend: status). If user is null - returns null.
     * @param username owner of friend list
     */
    @Override
    public List<String> getFriendList(String username) {
        List<FriendListEntry> friendEntryList = getFriendEntryList(username);
        if (friendEntryList == null) return null;
        List<String> friendList = new ArrayList<>();
        for (FriendListEntry entry: friendEntryList) {
            friendList.add(entry.getFriend().getUsername()+": "+entry.getStatus());
        }
        return friendList;
    }
    /**
     * Returns names of friends of a given user. If user is null - returns null.
     * @param username owner of friend list
     */
    @Override
    public List<String> getFriendNameList(String username) {
        List<FriendListEntry> friendEntryList = getFriendEntryList(username);
        if (friendEntryList == null) return null;
        return getNameListFromEntryList(friendEntryList);
    }
    /**
     * Returns list of friend requests as entities. If user is null - returns null.
     * @param username owner of friend list
     */
    @Override
    public List<FriendListEntry> getFriendEntryList(String username) {
        User user = userService.findByUsername(username);
        if (user!=null) {
            user.setFriends(friendListRepository.getFriendList(user));
            return user.getFriends();
        }else return null;
    }

    /**
     * Util method for getting usernames of friends from entries
     */
    private List<String> getNameListFromEntryList(List<FriendListEntry> entryList){
        List<String> friendList = new ArrayList<>();
        entryList.forEach(entry -> friendList.add(entry.getFriend().getUsername()));
        return friendList;
    }
}
