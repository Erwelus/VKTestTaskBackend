package com.ervelus.repository;

import com.ervelus.infrastructure.annotations.Component;
import com.ervelus.infrastructure.annotations.InjectProperty;
import com.ervelus.infrastructure.annotations.PostConstruct;
import lombok.Setter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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

    @PostConstruct
    public void init(){
        try {
            this.connection = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
        }catch (SQLException e){
            System.err.println("Failed to connect to db. Check properties values");
            System.exit(1);
        }
    }

    public Connection getConnection(){return this.connection;}
}
