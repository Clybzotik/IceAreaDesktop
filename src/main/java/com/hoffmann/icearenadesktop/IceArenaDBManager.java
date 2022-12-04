/**
 * Ice Area Administrator program.
 * @author hoffmann
 * @version 1.0
*/
package com.hoffmann.icearenadesktop;

import java.sql.*;
import java.util.Timer;
import java.util.TimerTask;

public class IceArenaDBManager {
    /**Declaration <b>String</b> with MySQL DataBase. */
    private final static String URL = "jdbc:mysql://localhost:3306/icearena";
    /**Declaration <b>String</b> with MySQL user with root permissions.*/
    private final static String USER = "root";
    /**Declaration <b>String</b> with user password.  */
    private final static String PASSWORD = "";

    /**Variable to storage <b>{@link Connection}</b> with DataBase.*/
    private static Connection connection;

    /**Creates new {@link Connection} and return it.
     *If connection create failed shutdown process.
     * @return {@link #connection}*/
    public Connection getConnection() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("DataBase connection failed.");
            System.exit(1);
        }
        return connection;
    }
    /**Creates new query which doesn't return any results.
     * @param query {@link String} with our query to DataBase*/
    public void execQuery(String query) throws SQLException {
        connection = getConnection();
        Statement statement = connection.createStatement();
        statement.execute(query);
        statement.close();
        connection.close();

    }

    /**Creates new query which return {@link ResultSet};
     * @param query {@link String} with our query to DataBase
     * @return {@link ResultSet}*/
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
