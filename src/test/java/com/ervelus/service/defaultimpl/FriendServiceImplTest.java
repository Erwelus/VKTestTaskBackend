package com.ervelus.service.defaultimpl;

import com.ervelus.model.FriendListEntry;
import com.ervelus.model.FriendStatus;
import com.ervelus.model.User;
import com.ervelus.repository.FriendListRepository;
import com.ervelus.service.UserService;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

class FriendServiceImplTest {
    static String username;
    static String friendName;
    static String userNotFriend;
    static String incomingFriendName;
    static User firstUser;
    static User secondUser;
    static User thirdUser;
    static User incomingUser;
    static FriendListRepository friendListRepositoryMock;
    static UserService userServiceMock;
    static List<FriendListEntry> notEmptyList;
    static List<FriendListEntry> emptyList;
    static FriendListEntry friendListEntry;
    static FriendListEntry incomingFriendEntry;
    static FriendListEntry reverseFriendEntry;
    FriendServiceImpl friendService;

    @BeforeAll
    static void init(){
        username = "test_user";
        friendName = "test_friend";
        userNotFriend = "test_friend_3";
        incomingFriendName = "test_friend_4";
        firstUser = new User(1, username, "password");
        secondUser = new User(2, friendName, "password");
        thirdUser = new User(3, userNotFriend, "password");
        incomingUser = new User(4, incomingFriendName, "password");
        emptyList = new ArrayList<>();
        notEmptyList = new ArrayList<>();
        friendListEntry = new FriendListEntry(firstUser, secondUser, FriendStatus.ACCEPTED);
        reverseFriendEntry = new FriendListEntry(secondUser, firstUser, FriendStatus.ACCEPTED);
        incomingFriendEntry= new FriendListEntry(firstUser, incomingUser, FriendStatus.INCOMING);
        notEmptyList.add(friendListEntry);
        notEmptyList.add(reverseFriendEntry);
        notEmptyList.add(incomingFriendEntry);
        friendListRepositoryMock = Mockito.mock(FriendListRepository.class);
        userServiceMock = Mockito.mock(UserService.class);
        Mockito.when(friendListRepositoryMock.getFriendList(firstUser)).thenReturn(notEmptyList);
        Mockito.when(friendListRepositoryMock.getFriendList(secondUser)).thenReturn(notEmptyList);
        Mockito.when(friendListRepositoryMock.getFriendList(thirdUser)).thenReturn(emptyList);
        Mockito.when(friendListRepositoryMock.getFriendList(null)).thenReturn(null);
        Mockito.when(userServiceMock.findByUsername(username)).thenReturn(firstUser);
        Mockito.when(userServiceMock.findByUsername(friendName)).thenReturn(secondUser);
        Mockito.when(userServiceMock.findByUsername(userNotFriend)).thenReturn(thirdUser);
        Mockito.when(userServiceMock.findByUsername(incomingFriendName)).thenReturn(incomingUser);
        Mockito.when(userServiceMock.findByUsername("username")).thenReturn(null);
        Mockito.when(userServiceMock.findByUsername(null)).thenReturn(null);
    }

    @BeforeEach
    void initFriendService(){
        friendService = new FriendServiceImpl();
        friendService.setUserService(userServiceMock);
        friendService.setFriendListRepository(friendListRepositoryMock);
    }

    @Test
    void sendFriendRequest() {
        Assertions.assertFalse(friendService.sendFriendRequest(username, friendName));
        Assertions.assertFalse(friendService.sendFriendRequest(friendName, username));

        Assertions.assertTrue(friendService.sendFriendRequest(username, userNotFriend));
        Assertions.assertTrue(friendService.sendFriendRequest(userNotFriend, username));

        Assertions.assertFalse(friendService.sendFriendRequest(username, "username"));
        Assertions.assertFalse(friendService.sendFriendRequest(username, null));
    }

    @Test
    void acceptFriendRequest() {
        Assertions.assertTrue(friendService.acceptFriendRequest(username, incomingFriendName));

        Assertions.assertFalse(friendService.acceptFriendRequest(username, friendName));
        Assertions.assertFalse(friendService.acceptFriendRequest(friendName, username));
        Assertions.assertFalse(friendService.acceptFriendRequest(username, userNotFriend));
        Assertions.assertFalse(friendService.acceptFriendRequest(username, "username"));
        Assertions.assertFalse(friendService.acceptFriendRequest(username, null));
    }

    @Test
    void rejectFriendRequest() {
        Assertions.assertTrue(friendService.rejectFriendRequest(username, incomingFriendName));

        Assertions.assertFalse(friendService.rejectFriendRequest(username, friendName));
        Assertions.assertFalse(friendService.rejectFriendRequest(friendName, username));
        Assertions.assertFalse(friendService.rejectFriendRequest(username, userNotFriend));
        Assertions.assertFalse(friendService.rejectFriendRequest(username, "username"));
        Assertions.assertFalse(friendService.rejectFriendRequest(username, null));
    }

    @Test
    void getFriendList() {
        List<String> nameList = new ArrayList<>();
        List<String> emptyNameList = new ArrayList<>();
        notEmptyList.forEach(item -> nameList.add(item.getFriend().getUsername()+": "+item.getStatus()));
        Assertions.assertEquals(nameList, friendService.getFriendList(username));
        Assertions.assertEquals(emptyNameList, friendService.getFriendList(userNotFriend));
        Assertions.assertNull(friendService.getFriendList(null));
    }

    @Test
    void getFriendNameList() {
        List<String> nameList = new ArrayList<>();
        List<String> emptyNameList = new ArrayList<>();
        notEmptyList.forEach(item -> nameList.add(item.getFriend().getUsername()));
        Assertions.assertEquals(nameList, friendService.getFriendNameList(username));
        Assertions.assertEquals(emptyNameList, friendService.getFriendNameList(userNotFriend));
        Assertions.assertNull(friendService.getFriendNameList(null));
    }

    @Test
    void getFriendEntryList() {
        Assertions.assertEquals(notEmptyList, friendService.getFriendEntryList(username));
        Assertions.assertEquals(emptyList, friendService.getFriendEntryList(userNotFriend));
        Assertions.assertNull(friendService.getFriendEntryList(null));
    }
}