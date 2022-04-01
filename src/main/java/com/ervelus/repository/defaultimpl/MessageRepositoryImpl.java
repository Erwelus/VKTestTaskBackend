package com.ervelus.repository.defaultimpl;

import com.ervelus.infrastructure.annotations.Component;
import com.ervelus.infrastructure.annotations.InjectByType;
import com.ervelus.model.Message;
import com.ervelus.model.User;
import com.ervelus.repository.DBConnector;
import com.ervelus.repository.MessageRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Component
public class MessageRepositoryImpl implements MessageRepository {
    @InjectByType
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
        } catch (SQLException e) {
            System.err.println("Failed to save message into DB");
        }
    }

    @Override
    public List<Message> loadChat(User userFrom, User userTo) {
        List<Message> messages = new ArrayList<>();
        try {
            Statement statement = connector.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("select content from message_vk order by record_date desc limit 10");
            while (resultSet.next()){
                Message message = new Message();
                message.setUserFrom(userFrom);
                message.setUserTo(userTo);
                message.setContent(resultSet.getString("content"));
                messages.add(message);
            }
            statement.close();
        } catch (SQLException e) {
            System.err.println("Failed to load chat from DB");
        }
        return messages;
    }
}
