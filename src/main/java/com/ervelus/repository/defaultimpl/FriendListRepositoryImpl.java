package com.ervelus.repository.defaultimpl;

import com.ervelus.infrastructure.annotations.Component;
import com.ervelus.infrastructure.annotations.InjectByType;
import com.ervelus.model.FriendListEntry;
import com.ervelus.model.FriendStatus;
import com.ervelus.model.User;
import com.ervelus.repository.DBConnector;
import com.ervelus.repository.FriendListRepository;
import lombok.Setter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
/**
 * Default implementation of FriendListRepository.
 * Annotated with @Component, used for injection into services
 * Provides access for SQL DB
 */
@Component
public class FriendListRepositoryImpl implements FriendListRepository {
    /**
     * Provider of connection to DB
     */
    @InjectByType
    @Setter
    private DBConnector connector;
    /**
     * Saves friend request into DB, business logic mistakes as null will not be saved
     */
    @Override
    public void saveFriendRequest(FriendListEntry entry) {
        try {
            PreparedStatement preparedStatement = connector.getConnection().prepareStatement("insert into friend_vk (owner, friend, status) values (?,?,?)");
            preparedStatement.setInt(1, entry.getOwner().getId());
            preparedStatement.setInt(2, entry.getFriend().getId());
            preparedStatement.setString(3, entry.getStatus().toString());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException | NullPointerException e) {
            System.err.println("Failed to save friend request into DB");
        }
    }

    /**
     * Updates status of friend request
     */
    @Override
    public void updateFriendRequest(FriendListEntry entry) {
        try {
            PreparedStatement preparedStatement = connector.getConnection().prepareStatement("update friend_vk set status = ? where owner = ? and friend = ?");
            preparedStatement.setString(1, entry.getStatus().toString());
            preparedStatement.setInt(2, entry.getOwner().getId());
            preparedStatement.setInt(3, entry.getFriend().getId());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException | NullPointerException e) {
            System.err.println("Failed to update friend request at DB");
        }
    }

    /**
     * Deletes friend request from DB
     */
    @Override
    public void deleteFriendRequestByBothUsers(User userFrom, User userTo) {
        try {
            PreparedStatement preparedStatement = connector.getConnection().prepareStatement("delete from friend_vk where owner = ? and friend = ?");
            preparedStatement.setInt(1, userFrom.getId());
            preparedStatement.setInt(2, userTo.getId());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException | NullPointerException e) {
            System.err.println("Failed to delete friend request from DB");
        }
    }

    /**
     * Loads friend list of given user
     * @return Friend list, empty list if no friends or business logic mistakes
     */
    @Override
    public List<FriendListEntry> getFriendList(User user) {
        List<FriendListEntry> entryList = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connector.getConnection().prepareStatement("select * from friend_vk where owner = ?");
            preparedStatement.setInt(1, user.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                FriendListEntry entry = new FriendListEntry();
                entry.setOwner(user);
                setFriendById(resultSet.getInt("friend"), entry);
                entry.setStatus(FriendStatus.valueOf(resultSet.getString("status").toUpperCase()));
                entryList.add(entry);
            }
            preparedStatement.close();
        } catch (SQLException | NullPointerException e) {
            System.err.println("Failed to load friend list from DB");
        }
        return entryList;
    }

    /**
     * Util method for getting user for getting user from DB by their ID
     * @param id id of the friend
     * @param entry FriendListEntry which requires friend to be set
     * @throws SQLException when user is not present in DB (does not happen due to DB restrictions)
     */
    private void setFriendById(Integer id, FriendListEntry entry) throws SQLException {
        Statement statement = connector.getConnection().createStatement();
        ResultSet userById = statement.executeQuery("select * from users_vk where id="+id);
        userById.next();
        entry.setFriend(new User(userById.getInt("id"), userById.getString("username"), userById.getString("password")));
    }
}
