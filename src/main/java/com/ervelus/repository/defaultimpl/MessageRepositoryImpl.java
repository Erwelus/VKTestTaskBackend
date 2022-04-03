package com.ervelus.repository.defaultimpl;

import com.ervelus.infrastructure.annotations.Component;
import com.ervelus.infrastructure.annotations.InjectByType;
import com.ervelus.model.Message;
import com.ervelus.model.User;
import com.ervelus.repository.DBConnector;
import com.ervelus.repository.MessageRepository;
import lombok.Setter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Component
public class MessageRepositoryImpl implements MessageRepository {
    @InjectByType
    @Setter
    private DBConnector connector;
    @Override
    public void save(Message message) {
        try {
            PreparedStatement preparedStatement = connector.getConnection().prepareStatement("insert into message_vk (user_from, user_to, content) values (?,?,?)");
            preparedStatement.setInt(1, message.getUserFrom().getId());
            preparedStatement.setInt(2, message.getUserTo().getId());
            preparedStatement.setString(3, message.getContent());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException | NullPointerException e) {
            System.err.println("Failed to save message into DB");
        }
    }

    @Override
    public List<Message> loadChat(User userFrom, User userTo) {
        List<Message> messages = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connector.getConnection().prepareStatement(
                    "select user_from, content from message_vk where user_from = ? and user_to = ? order by record_date desc limit 10");
            preparedStatement.setInt(1, userFrom.getId());
            preparedStatement.setInt(2, userTo.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                Message message = new Message();
                setUserFromById(resultSet.getInt("user_from"), message);
                message.setContent(resultSet.getString("content"));
                messages.add(message);
            }
            preparedStatement.close();
        } catch (SQLException | NullPointerException e) {
            System.err.println("Failed to load chat from DB");
        }
        return messages;
    }

    private void setUserFromById(Integer id, Message message) throws SQLException {
        Statement statement = connector.getConnection().createStatement();
        ResultSet userById = statement.executeQuery("select * from users_vk where id="+id);
        userById.next();
        message.setUserFrom(new User(userById.getInt("id"), userById.getString("username"), userById.getString("password")));
    }
}
