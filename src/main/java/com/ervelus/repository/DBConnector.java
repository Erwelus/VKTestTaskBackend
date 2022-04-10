package com.ervelus.repository;

import com.ervelus.infrastructure.annotations.Component;
import com.ervelus.infrastructure.annotations.InjectProperty;
import com.ervelus.infrastructure.annotations.PostConstruct;
import lombok.Setter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class used for connecting to the DB
 * Provides connection to DB for repositories
 * Requires info about DB in property file
 * Is annotated with @Component for injecting into repositories
 */
@Component
public class DBConnector {
    @InjectProperty
    private String dbURL;
    @InjectProperty
    private String dbUsername;
    @InjectProperty
    private String dbPassword;
    @Setter
    private Connection connection;

    /**
     * Method for connecting to DB
     * Invoked automatically after properties set
     */
    @PostConstruct
    public void init(){
        try {
            this.connection = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
            initDB();
        }catch (SQLException e){
            System.err.println("Failed to connect to db. Check properties values");
            System.exit(1);
        }
    }

    /**
     * Getter for the DB connection, used by repositories
     */
    public Connection getConnection(){return this.connection;}

    private void initDB() throws SQLException {
        Statement statement = getConnection().createStatement();
        statement.executeUpdate("create table if not exists users_vk(" +
                "id serial primary key, username text not null, passwword text not null)");

        statement.executeUpdate("create table if not exists friend_vk(" +
                "id serial primary key, " +
                "owner integer not null references users_vk on delete cascade on update cascade, " +
                "friend integer not null references users_vk on delete cascade on update cascade," +
                "status text)");

        statement.executeUpdate("create table if not exists message_vk(" +
                "id serial primary key, " +
                "user_from integer not null references users_vk on delete cascade on update cascade, " +
                "user_to integer not null references users_vk on delete cascade on update cascade," +
                "content text, record_date timestamp default now())");
    }
}
