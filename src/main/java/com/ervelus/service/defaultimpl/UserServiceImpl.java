package com.ervelus.service.defaultimpl;

import com.ervelus.infrastructure.annotations.Component;
import com.ervelus.infrastructure.annotations.InjectByType;
import com.ervelus.model.FriendListEntry;
import com.ervelus.model.FriendStatus;
import com.ervelus.model.User;
import com.ervelus.repository.FriendListRepository;
import com.ervelus.repository.UserRepository;
import com.ervelus.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;

@Component
public class UserServiceImpl implements UserService {
    @InjectByType
    private UserRepository userRepository;
    @InjectByType
    private FriendListRepository friendListRepository;

    @Override
    public void register(User user) {
        user.setPassword(DigestUtils.sha256Hex(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public void sendFriendRequest(User user, User friend) {
        FriendListEntry waitingEntry = new FriendListEntry(user, friend, FriendStatus.WAITING);
        FriendListEntry incomingEntry = new FriendListEntry(friend, user, FriendStatus.INCOMING);
        friendListRepository.saveFriendRequest(waitingEntry);
        friendListRepository.saveFriendRequest(incomingEntry);
    }

    @Override
    public void acceptFriendRequest(User user, User friend) {
        FriendListEntry incomingEntry = new FriendListEntry(user, friend, FriendStatus.ACCEPTED);
        FriendListEntry waitingEntry = new FriendListEntry(friend, user, FriendStatus.ACCEPTED);
        friendListRepository.updateFriendRequest(incomingEntry);
        friendListRepository.updateFriendRequest(waitingEntry);
    }

    @Override
    public void declineFriendRequest(User user, User friend) {
        friendListRepository.deleteFriendRequestByBothUsers(user, friend);
    }
}
