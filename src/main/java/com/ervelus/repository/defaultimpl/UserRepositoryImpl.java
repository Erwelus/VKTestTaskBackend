package com.ervelus.repository.defaultimpl;

import com.ervelus.infrastructure.annotations.Component;
import com.ervelus.infrastructure.annotations.InjectByType;
import com.ervelus.model.User;
import com.ervelus.repository.DBConnector;
import com.ervelus.repository.UserRepository;
import lombok.Setter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Default implementation of UserRepository.
 * Annotated with @Component, used for injection into services
 * Provides access for SQL DB
 */
@Component
public class UserRepositoryImpl implements UserRepository {
    /**
     * Provider of connection to DB
     */
    @InjectByType
    @Setter
    private DBConnector connector;

    /**
     * Saves user into DB, business logic mistakes as null user or non-unique username will not be saved
     */
    @Override
    public void save(User user) {
        try {
            PreparedStatement preparedStatement = connector.getConnection().prepareStatement("insert into users_vk (username, password) values (?,?)");
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException | NullPointerException e) {
            System.err.println("Failed to save user into DB");
        }
    }

    /**
     * Loads user from DB by their username, should be unique
     * @return User if is present, null otherwise
     */
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
