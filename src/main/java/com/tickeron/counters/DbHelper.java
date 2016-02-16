package com.tickeron.counters;

import java.sql.*;

/**
 * Created by slaviann on 16.02.16.
 */
public class DbHelper {


    private String url, username, password;

    private Statement statement;
    private Connection connection;
    private ResultSet resultSet;

    public Statement getStatement() {
        return statement;
    }

    public DbHelper(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public void connect() throws SQLException {
        connection = DriverManager.getConnection(url, username, password);
        statement = connection.createStatement();
    }


    public void disconnect() throws SQLException {
        connection.close();
        if(resultSet != null) resultSet.close();
        if(statement != null) statement.close();
        if(connection != null) connection.close();
    }



}
