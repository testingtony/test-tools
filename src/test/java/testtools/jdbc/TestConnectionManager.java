package testtools.jdbc;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import static org.mockito.Mockito.*;
import static testtools.jdbc.MockJDBCDriver.MOCKURL;

public class TestConnectionManager {

    private Connection mockConnection;
    private Properties dummyProperties = new Properties();

    @Rule
    public MockJDBCDriver mockDriver = new MockJDBCDriver();

    @Before
    public void setUp() throws SQLException {
        mockConnection = mock(Connection.class);
        mockDriver.register(mockConnection);

    }
    @Test
    public void connectionManagerTurnsOffCommits() throws SQLException{
        ConnectionManager connectionManager = new ConnectionManager(MOCKURL, dummyProperties);

        connectionManager.turnOffAutoCommit();

        verify(mockConnection, times(1)).setAutoCommit(false);
    }

    @Test
    public void connectionManagerTurnsOnCommits() throws SQLException{
        ConnectionManager connectionManager = new ConnectionManager(MOCKURL, dummyProperties);

        connectionManager.turnOnAutoCommit();

        verify(mockConnection, times(1)).setAutoCommit(true);
    }

    @Test
    public void connectionManagerCommits() throws SQLException{
        ConnectionManager connectionManager = new ConnectionManager(MOCKURL, dummyProperties);

        connectionManager.commit();

        verify(mockConnection, times(1)).commit();
    }

    @Test
    public void connectionManagerCloses() throws SQLException{
        ConnectionManager connectionManager = new ConnectionManager(MOCKURL, dummyProperties);

        connectionManager.commit();

        verify(mockConnection, times(1)).commit();
    }

    @Test
    public void connectionManagerPreparesStatements() throws SQLException {
        ConnectionManager connectionManager = new ConnectionManager(MOCKURL, dummyProperties);

        connectionManager.prepareStatement("any String");

        verify(mockConnection, times(1)).prepareStatement("any String");
    }

    @Test
    public void connectionManagerExecuteQueryCreatesAStatementAndExecutesQuery() throws SQLException {
        Statement mockStatement = mock(Statement.class);
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        ConnectionManager connectionManager = new ConnectionManager(MOCKURL, dummyProperties);

        connectionManager.executeQuery("any Query");

        verify(mockStatement, times(1)).executeQuery("any Query");
    }
}
