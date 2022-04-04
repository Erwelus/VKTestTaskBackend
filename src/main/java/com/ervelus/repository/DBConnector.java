package com.ervelus.repository;

import com.ervelus.infrastructure.annotations.Component;
import com.ervelus.infrastructure.annotations.InjectProperty;
import com.ervelus.infrastructure.annotations.PostConstruct;
import lombok.Setter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
        }catch (SQLException e){
            System.err.println("Failed to connect to db. Check properties values");
            System.exit(1);
        }
    }

    /**
     * Getter for the DB connection, used by repositories
     */
    public Connection getConnection(){return this.connection;}
}
