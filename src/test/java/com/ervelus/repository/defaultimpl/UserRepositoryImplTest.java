package com.ervelus.repository.defaultimpl;

import com.ervelus.model.User;
import com.ervelus.repository.DBConnector;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.*;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryImplTest {
    static DBConnector connector;
    static User correctUser;
    static String username;
    static User userForSave;
    static String saveUserName;
    static User nullUser;
    static String incorrectUsername;
    static String password;
    static String testDBURL;
    static String testDBUsername;
    static String testDBPassword;
    UserRepositoryImpl repository;

    @BeforeAll
    static void init() throws SQLException {
        connector = new DBConnector();

        connector.setConnection(DriverManager.getConnection(testDBURL, testDBUsername, testDBPassword));
        username = "test_user";
        incorrectUsername = "incorrect_user";
        saveUserName = "save_user";
        password = "password";
        userForSave = new User(saveUserName, password);
        correctUser = new User(100, username, password);
        nullUser = new User();
    }


    @BeforeEach
    void setUp() {
        repository = new UserRepositoryImpl();
        repository.setConnector(connector);
    }

    @AfterAll
    static void deleteUsersFromDB() throws SQLException {
        Statement statement = connector.getConnection().createStatement();
        statement.executeUpdate("delete from users_vk where username = 'save_user' or username = 'test_user'");
    }

    @Test
    void save() throws SQLException {
        int expectedCount = countRecords() + 1;
        repository.save(userForSave);
        repository.save(nullUser);
        repository.save(null);
        Assertions.assertEquals(selectUserFromDB(), userForSave);
        Assertions.assertEquals(expectedCount, countRecords());
    }

    @Test
    void findUserByUsername() throws SQLException {
        insertUserToDB();
        assertEquals(correctUser, repository.findUserByUsername(username));
        assertNull(repository.findUserByUsername(incorrectUsername));
        assertNull(repository.findUserByUsername(null));
    }

    User selectUserFromDB() throws SQLException {
        Statement statement = connector.getConnection().createStatement();
        ResultSet set = statement.executeQuery("select * from users_vk where username = 'save_user'");
        set.next();
        return new User(set.getString("username"), set.getString("password"));
    }

    int countRecords() throws SQLException {
        Statement statement = connector.getConnection().createStatement();
        ResultSet set = statement.executeQuery("select count (*) from users_vk");
        set.next();
        return set.getInt(1);
    }

    void insertUserToDB() throws SQLException {
        Statement statement = connector.getConnection().createStatement();
        statement.executeUpdate("insert into users_vk values (100, 'test_user', 'password')");
    }

}