package com.tasktracker.repository;

import com.tasktracker.model.Task;
import com.tasktracker.model.TaskStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class SqlServerTaskRepository implements TaskRepository{
    private final String connectionUrl;
    private final String username;
    private final String password;

    public SqlServerTaskRepository(String connectionUrl, String username, String password){
        this.connectionUrl = connectionUrl;
        this.username = username;
        this.password = password;
        validateConnection();
    }

    public SqlServerTaskRepository(Properties properties){
        this(
                properties.getProperty("mssql.url", "jdbc:sqlserver://localhost:1433;"+
                        "databaseName=TaskTrackedDB;trustServerCertificate=true"),
                properties.getProperty("mssql.username", "sa"),
                properties.getProperty("mssql.password", ""));
    }

    public SqlServerTaskRepository(){
        this(
                "jdbc:sqlserver://localhost:1433;databaseName=TaskTrackerDB;trustServiceCertificate=true",
                "sa", ""
        );
    }

    private void validateConnection(){

    }

    private Connection getConnection(){
        return null;
    }

    @Override
    public Task save(Task task){
        return null;
    }

    @Override
    public Optional<Task> findById(long id){
        return null;
    }

    @Override
    public List<Task> findAll(){
        return null;
    }

    @Override
    public Task update(Task task){
        return null;
    }

    @Override
    public boolean delete(long id){
        return false;
    }

    @Override
    public boolean existsById(long id){
        return false;
    }


}
