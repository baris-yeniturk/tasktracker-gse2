package com.tasktracker.repository;

import com.tasktracker.model.Task;
import com.tasktracker.model.TaskStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class SqlServerTaskRepository{
    private final String connectionUrl;
    private final String username;
    private final String password;

    public SqlServerTaskRepository(String connectionUrl, String username, String password){
        this.connectionUrl = connectionUrl;
        this.username = username;
        this.password = password;
        validateConnection();
    }


}
