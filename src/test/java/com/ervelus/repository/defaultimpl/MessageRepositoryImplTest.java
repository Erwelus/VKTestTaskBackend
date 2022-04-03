package com.ervelus.repository.defaultimpl;

import com.ervelus.model.Message;
import com.ervelus.model.User;
import com.ervelus.repository.DBConnector;
import org.junit.jupiter.api.*;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MessageRepositoryImplTest {

    static DBConnector connector;
    static String testDBURL;
    static String testDBUsername;
    static String testDBPassword;
    static Message nullMessage;
    static Message correctMessage;
    static String messageText;
    static String correctMessageText;
    static List<Message> emptyList;
    static List<Message> notEmptyList;
    static Message messageToSave;
    static User userFrom;
    static User userTo;
    static User userWithEmptyChat;
    static String userFromUsername;
    static String userToUsername;
    static String usernameEmptyChat;
    static String password;
    MessageRepositoryImpl repository;

    @BeforeAll
    static void init() throws SQLException {
        connector = new DBConnector();

        connector.setConnection(DriverManager.getConnection(testDBURL, testDBUsername, testDBPassword));
        nullMessage = new Message();
        userFromUsername = "from_user";
        userToUsername = "to_user";
        password = "password";
        messageText="test_text";
        correctMessageText = "test_text_2";
        usernameEmptyChat = "empty_user";
        userWithEmptyChat = new User(3, usernameEmptyChat, password);
        userFrom = new User(1, userFromUsername, password);
        userTo = new User(2, userToUsername, password);
        correctMessage = new Message(userFrom, null, correctMessageText);
        messageToSave =  new Message(userFrom, userWithEmptyChat, messageText);
        emptyList = new ArrayList<>();
        notEmptyList = new ArrayList<>();
        notEmptyList.add(correctMessage);
    }


    @AfterAll
    static void deleteMessagesFromDB() throws SQLException {
        Statement statement = connector.getConnection().createStatement();
        statement.executeUpdate(
                "delete from message_vk where user_from = 1 and (content = 'test_text' or content = 'test_text_2')");
    }

    @BeforeEach
    void setUp() {
        repository = new MessageRepositoryImpl();
        repository.setConnector(connector);
    }
    @Test
    void save() throws SQLException {
        int expectedCount = countRecords() + 1;
        repository.save(messageToSave);
        repository.save(nullMessage);
        repository.save(null);
        ResultSet set = selectMessageFromDB();
        set.next();
        Assertions.assertEquals(set.getInt("user_from"), messageToSave.getUserFrom().getId());
        Assertions.assertEquals(set.getInt("user_to"), messageToSave.getUserTo().getId());
        Assertions.assertEquals(set.getString("content"), messageToSave.getContent());
        Assertions.assertEquals(expectedCount, countRecords());
    }

    @Test
    void loadChat() throws SQLException {
        insertMessageToDB();
        assertEquals(notEmptyList.size(), repository.loadChat(userFrom, userTo).size());
        assertEquals(correctMessageText, repository.loadChat(userFrom, userTo).get(0).getContent());
        assertEquals(emptyList, repository.loadChat(userWithEmptyChat, userTo));
        assertEquals(emptyList, repository.loadChat(null, null));
    }

    ResultSet selectMessageFromDB() throws SQLException {
        Statement statement = connector.getConnection().createStatement();
        return statement.executeQuery("select * from message_vk where user_from = 1 and content = 'test_text'");
    }

    int countRecords() throws SQLException {
        Statement statement = connector.getConnection().createStatement();
        ResultSet set = statement.executeQuery("select count (*) from message_vk");
        set.next();
        return set.getInt(1);
    }

    void insertMessageToDB() throws SQLException {
        Statement statement = connector.getConnection().createStatement();
        statement.executeUpdate("insert into message_vk values (100, 1, 2, 'test_text_2')");
    }
}