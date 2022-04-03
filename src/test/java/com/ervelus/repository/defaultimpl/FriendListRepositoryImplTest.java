package com.ervelus.repository.defaultimpl;

import com.ervelus.model.FriendListEntry;
import com.ervelus.model.FriendStatus;
import com.ervelus.model.User;
import com.ervelus.repository.DBConnector;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FriendListRepositoryImplTest {
    static DBConnector connector;
    static String testDBURL;
    static String testDBUsername;
    static String testDBPassword;
    static FriendListEntry entryToSave;
    static FriendListEntry entryToUpdate;
    static FriendListEntry entryToDelete;
    static FriendListEntry entryToGet;
    static List<FriendListEntry> emptyList;
    static List<FriendListEntry> notEmptyList;
    static User owner;
    static User friendOfOwnerFirst;
    static User friendOfOwnerSecond;
    FriendListRepositoryImpl repository;

    @BeforeAll
    static void init() throws SQLException {
        connector = new DBConnector();

        connector.setConnection(DriverManager.getConnection(testDBURL, testDBUsername, testDBPassword));
        owner = new User(1, "name", "pass");
        friendOfOwnerFirst = new User(2, "name", "pass");
        friendOfOwnerSecond = new User(3, "name", "pass");
        entryToSave = new FriendListEntry(owner,friendOfOwnerFirst, FriendStatus.ACCEPTED);
        entryToUpdate = new FriendListEntry(friendOfOwnerFirst,owner, FriendStatus.WAITING);
        entryToDelete = new FriendListEntry(owner,friendOfOwnerSecond, FriendStatus.INCOMING);
        entryToGet = new FriendListEntry(friendOfOwnerSecond,friendOfOwnerFirst, FriendStatus.ACCEPTED);
        emptyList = new ArrayList<>();
        notEmptyList = new ArrayList<>();
        notEmptyList.add(entryToGet);
    }

    @AfterAll
    static void deleteEntries() throws SQLException {
        Statement statement = connector.getConnection().createStatement();
        statement.executeUpdate(
                "delete from friend_vk where owner = 1 or owner = 2 or owner = 3");
    }

    @BeforeEach
    void setUp(){
        repository = new FriendListRepositoryImpl();
        repository.setConnector(connector);
    }

    @Test
    void saveFriendRequest() throws SQLException {
        repository.saveFriendRequest(entryToSave);
        Assertions.assertTrue(checkEntryFromDBByExpected(entryToSave));
    }

    @Test
    void updateFriendRequest() throws SQLException {
        insertEntryToDBByExpected(entryToUpdate);
        repository.updateFriendRequest(entryToUpdate);
        Assertions.assertTrue(checkEntryFromDBByExpected(entryToUpdate));
    }

    @Test
    void deleteFriendRequestByBothUsers() throws SQLException {
        insertEntryToDBByExpected(entryToDelete);
        repository.deleteFriendRequestByBothUsers(owner, friendOfOwnerSecond);
        Assertions.assertFalse(checkEntryFromDBByExpected(entryToDelete));
    }

    @Test
    void getFriendList() throws SQLException {
        insertEntryToDBByExpected(entryToGet);
        Assertions.assertEquals(notEmptyList.size(), repository.getFriendList(entryToGet.getOwner()).size());
        Assertions.assertEquals(entryToGet.getStatus(), repository.getFriendList(entryToGet.getOwner()).get(0).getStatus());
        Assertions.assertEquals(emptyList, repository.getFriendList(null));
    }

    boolean checkEntryFromDBByExpected(FriendListEntry expected) throws SQLException {
        PreparedStatement statement = connector.getConnection().prepareStatement("select * from friend_vk where owner=? and friend=? and status=?");
        statement.setInt(1, expected.getOwner().getId());
        statement.setInt(2, expected.getFriend().getId());
        statement.setString(3, expected.getStatus().toString());
        ResultSet set = statement.executeQuery();
        return set.next();
    }

    void insertEntryToDBByExpected(FriendListEntry expected) throws SQLException {
        PreparedStatement statement = connector.getConnection().prepareStatement("insert into friend_vk (owner, friend, status) values (?,?,?)");
        statement.setInt(1, expected.getOwner().getId());
        statement.setInt(2, expected.getFriend().getId());
        statement.setString(3, expected.getStatus().toString());
        statement.executeUpdate();
    }
}