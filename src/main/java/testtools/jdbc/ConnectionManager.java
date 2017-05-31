package testtools.jdbc;

import java.sql.*;
import java.util.Properties;

public class ConnectionManager implements AutoCloseable{
    private Connection connection;
    private String user;
    private String password;

    public ConnectionManager(String user, String password, String url, Properties properties) throws SQLException {
        Properties info = new Properties(properties);
        if (user != null) {
            info.setProperty("user", user);
        }
        if (password != null) {
            info.setProperty("password", password);
        }
        connection = DriverManager.getConnection(url, info);
    }

    public ConnectionManager(String user, String password, String url) throws SQLException {
        connection = DriverManager.getConnection(url, user, password);
    }

    public ConnectionManager(String url, Properties properties) throws SQLException {
        connection = DriverManager.getConnection(url, properties);
    }

    public Statement createStatement() throws SQLException {
        return connection.createStatement();
    }

    public ResultSet executeQuery(String command) throws SQLException {
        Statement statement = createStatement();
        ResultSet results = statement.executeQuery(command);
        return results;
    }

    public PreparedStatement prepareStatement(String query) throws SQLException {
        return connection.prepareStatement(query);
    }

    public void turnOffAutoCommit() {
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void turnOnAutoCommit() {
        try {
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void commit() {
        try {
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
