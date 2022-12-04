package com.hoffmann.icearenadesktop;

import java.sql.*;
import java.util.Timer;
import java.util.TimerTask;

public class IceArenaDBManager {
    private final static String URL = "jdbc:mysql://localhost:3306/icearena";
    private final static String USER = "root";
    private final static String PASSWORD = "";

    private static Connection connection;

    public Connection getConnection() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("DataBase connection failed.");
            System.exit(1);
        }
        return connection;
    }

    public void execQuery(String query) throws SQLException {
        connection = getConnection();
        Statement statement = connection.createStatement();
        statement.execute(query);
        statement.close();
        connection.close();

    }

    public ResultSet execQueryWithResult(String query) throws SQLException {
        connection = getConnection();
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(query);

        new Timer().schedule( new TimerTask() {
            public void run() {
                try {
                    result.close();
                    statement.close();
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        },1000 );
        return result;
    }
}
