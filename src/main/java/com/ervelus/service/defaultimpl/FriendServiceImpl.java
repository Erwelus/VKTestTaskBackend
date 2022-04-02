package com.ervelus.service.defaultimpl;

import com.ervelus.infrastructure.annotations.InjectByType;
import com.ervelus.model.FriendListEntry;
import com.ervelus.model.FriendStatus;
import com.ervelus.model.User;
import com.ervelus.repository.FriendListRepository;
import com.ervelus.service.FriendService;
import com.ervelus.service.UserService;

import java.util.ArrayList;
import java.util.List;

public class FriendServiceImpl implements FriendService {
    @InjectByType
    private FriendListRepository friendListRepository;
    @InjectByType
    private UserService userService;

    @Override
    public boolean sendFriendRequest(String username, String friendName) {
        User user = userService.findByUsername(username);
        User friend = userService.findByUsername(friendName);
        if (user!=null && friend !=null){
            FriendListEntry waitingEntry = new FriendListEntry(user, friend, FriendStatus.WAITING);
            FriendListEntry incomingEntry = new FriendListEntry(friend, user, FriendStatus.INCOMING);
            friendListRepository.saveFriendRequest(waitingEntry);
            friendListRepository.saveFriendRequest(incomingEntry);
            return true;
        } else return false;

    }

    @Override
    public boolean acceptFriendRequest(String username, String friendName) {
        User user = userService.findByUsername(username);
        User friend = userService.findByUsername(friendName);
        if (user!=null && friend !=null) {
            FriendListEntry incomingEntry = new FriendListEntry(user, friend, FriendStatus.ACCEPTED);
            FriendListEntry waitingEntry = new FriendListEntry(friend, user, FriendStatus.ACCEPTED);
            friendListRepository.updateFriendRequest(incomingEntry);
            friendListRepository.updateFriendRequest(waitingEntry);
            return true;
        }else return false;
    }

    @Override
    public boolean rejectFriendRequest(String username, String friendName) {
        User user = userService.findByUsername(username);
        User friend = userService.findByUsername(friendName);
        if (user!=null && friend !=null) {
            friendListRepository.deleteFriendRequestByBothUsers(user, friend);
            friendListRepository.deleteFriendRequestByBothUsers(friend, user);
            return true;
        }else return false;
    }

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

    @Override
    public List<String> getFriendNameList(String username) {
        List<FriendListEntry> friendEntryList = getFriendEntryList(username);
        if (friendEntryList == null) return null;
        List<String> friendList = new ArrayList<>();
        for (FriendListEntry entry: friendEntryList) {
            friendList.add(entry.getFriend().getUsername());
        }
        return friendList;
    }

    @Override
    public List<FriendListEntry> getFriendEntryList(String username) {
        User user = userService.findByUsername(username);
        if (user!=null) {
            if (user.getFriends() == null) {
                user.setFriends(friendListRepository.getFriendList(user));
            }
            return user.getFriends();
        }else return null;
    }
}
