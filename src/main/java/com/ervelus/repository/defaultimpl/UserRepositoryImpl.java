package com.ervelus.repository.defaultimpl;

import com.ervelus.infrastructure.annotations.Component;
import com.ervelus.infrastructure.annotations.InjectByType;
import com.ervelus.model.User;
import com.ervelus.repository.DBConnector;
import com.ervelus.repository.UserRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class UserRepositoryImpl implements UserRepository {
    @InjectByType
    private DBConnector connector;
    @Override
    public void save(User user) {
        try {
            PreparedStatement preparedStatement = connector.getConnection().prepareStatement("insert into users_vk (username, password) values (?,?)");
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            System.err.println("Failed to save user into DB");
        }
    }

    @Override
    public User findUserByUsername(String username) {
        User user = null;
        try {
            PreparedStatement statement = connector.getConnection().prepareStatement("select * from users_vk where username = ?");
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){
                user = new User(resultSet.getInt("id"), resultSet.getString("username"), resultSet.getString("password"));
            }
        } catch (SQLException e) {
            System.err.println("Failed to load user from DB");
        }
        return user;
    }
}
