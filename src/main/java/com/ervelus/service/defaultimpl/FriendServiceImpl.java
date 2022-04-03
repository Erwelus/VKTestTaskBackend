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

public class FriendServiceImpl implements FriendService {
    @InjectByType
    @Setter
    private FriendListRepository friendListRepository;
    @InjectByType
    @Setter
    private UserService userService;

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
        return getNameListFromEntryList(friendEntryList);
    }

    @Override
    public List<FriendListEntry> getFriendEntryList(String username) {
        User user = userService.findByUsername(username);
        if (user!=null) {
            user.setFriends(friendListRepository.getFriendList(user));
            return user.getFriends();
        }else return null;
    }

    private List<String> getNameListFromEntryList(List<FriendListEntry> entryList){
        List<String> friendList = new ArrayList<>();
        entryList.forEach(entry -> friendList.add(entry.getFriend().getUsername()));
        return friendList;
    }
}
