package testtools.jdbc;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

import static java.sql.DriverManager.deregisterDriver;
import static java.sql.DriverManager.registerDriver;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/*
* JUnit4 rule to make mocking JDBC drivers easy.
* To use, add a @Rule and, in the tests, call register(String url, Connection connection)
* where the (mock) connection will be returned whenever
* DriverManager.getConnection(url, properties) is called.
 */
public class MockJDBCDriver extends TestWatcher {
    public final static String MOCKURL = "jdbc:mock";

    private List<Driver> drivers = new LinkedList<>();

    public Connection register(String url, Connection connection) throws SQLException {
        final Driver mockDriver = registeredDriverForURL(url, connection);
        drivers.add(mockDriver);
        return connection;
    }

    public Connection register(Connection connection) throws SQLException {
        return register(MOCKURL, connection);
    }

    public Connection register(String url) throws SQLException {
        return register(url, mock(Connection.class));
    }

    public Connection register() throws SQLException {
        return register(MOCKURL);
    }

    private Driver registeredDriverForURL(final String url, final Connection connection) throws SQLException {
        final Driver mockDriver = mock(Driver.class);
        when(mockDriver.connect(eq(url), any())).thenReturn(connection);
        registerDriver(mockDriver);
        return mockDriver;
    }

    /* implemented as a rule because you need to deregister at the end of each test */
    @Override
    protected void finished(Description description) {
        super.finished(description);
        for (Driver driver : drivers) {
            try {
                deregisterDriver(driver);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}