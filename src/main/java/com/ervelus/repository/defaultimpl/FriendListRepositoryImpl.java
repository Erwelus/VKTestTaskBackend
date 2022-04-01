package com.ervelus.repository.defaultimpl;

import com.ervelus.infrastructure.annotations.Component;
import com.ervelus.infrastructure.annotations.InjectByType;
import com.ervelus.model.FriendListEntry;
import com.ervelus.model.FriendStatus;
import com.ervelus.model.User;
import com.ervelus.repository.DBConnector;
import com.ervelus.repository.FriendListRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Component
public class FriendListRepositoryImpl implements FriendListRepository {
    @InjectByType
    private DBConnector connector;

    @Override
    public void saveFriendRequest(FriendListEntry entry) {
        try {
            PreparedStatement preparedStatement = connector.getConnection().prepareStatement("insert into friend_vk (owner, friend, status) values (?,?,?)");
            preparedStatement.setInt(1, entry.getOwner().getId());
            preparedStatement.setInt(2, entry.getFriend().getId());
            preparedStatement.setString(3, entry.getStatus().toString());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            System.err.println("Failed to save friend request into DB");
        }
    }

    @Override
    public void updateFriendRequest(FriendListEntry entry) {
        try {
            PreparedStatement preparedStatement = connector.getConnection().prepareStatement("update friend_vk set status = ?");
            preparedStatement.setString(1, entry.getStatus().toString());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            System.err.println("Failed to update friend request at DB");
        }
    }

    @Override
    public void deleteFriendRequestByBothUsers(User userFrom, User userTo) {
        try {
            PreparedStatement preparedStatement = connector.getConnection().prepareStatement("delete from friend_vk where owner = ? and another = ?");
            preparedStatement.setInt(1, userFrom.getId());
            preparedStatement.setInt(2, userTo.getId());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            System.err.println("Failed to delete friend request from DB");
        }
    }

    @Override
    public List<FriendListEntry> getFriendList(User user) {
        List<FriendListEntry> entryList = new ArrayList<>();
        try {
            Statement statement = connector.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("select * from friend_vk");
            while (resultSet.next()){
                FriendListEntry entry = new FriendListEntry();
                entry.setOwner(user);
                setFriendById(statement, resultSet.getInt("id"), entry);
                entry.setStatus(FriendStatus.valueOf(resultSet.getString("status").toUpperCase()));
                entryList.add(entry);
            }
            statement.close();
        } catch (SQLException e) {
            System.err.println("Failed to load friend list from DB");
        }
        return entryList;
    }

    private void setFriendById(Statement statement, Integer id, FriendListEntry entry) throws SQLException {
        ResultSet userById = statement.executeQuery("select * from users_vk where id="+id);
        userById.next();
        entry.setFriend(new User(userById.getInt("id"), userById.getString("username"), userById.getString("password")));
    }
}
