package testtools.jdbc;

import org.junit.Rule;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class TestMockJDBCDriver {

    @Rule
    public MockJDBCDriver mockJDBCDriver = new MockJDBCDriver();

    @Test
    public void differentTestsCanUseTheSameMockURL1() throws SQLException {
        Connection c = mock(Connection.class);
        when(c.toString()).thenReturn("1");

        mockJDBCDriver.register("jdbc:one", c);
        Connection nc = DriverManager.getConnection("jdbc:one");

        assertThat(nc, hasToString("1"));
    }

    @Test
    public void differentTestsCanUseTheSameMockURL2() throws SQLException {
        Connection c = mock(Connection.class);
        when(c.toString()).thenReturn("2");

        mockJDBCDriver.register("jdbc:one", c);
        Connection nc = DriverManager.getConnection("jdbc:one");

        assertThat(nc, hasToString("2"));
    }

    @Test
    public void differentTestsCanUseTheSameMockURL3() throws SQLException {
        Connection c = mock(Connection.class);
        when(c.toString()).thenReturn("3");

        mockJDBCDriver.register("jdbc:one", c);
        Connection nc = DriverManager.getConnection("jdbc:one");

        assertThat(nc, hasToString("3"));
    }

    @Test
    public void driverManagerCanBeCalledWithUsernameAndPassword() throws SQLException {
        Connection c = mock(Connection.class);
        when(c.toString()).thenReturn("4");

        mockJDBCDriver.register(c);
        Connection nc = DriverManager.getConnection(MockJDBCDriver.MOCKURL, "user", "password");

        assertThat(nc, hasToString("4"));
    }

    @Test
    public void aTestCanRegisterMultipleMockURLs() throws SQLException {
        Connection c4 = mock(Connection.class);
        when(c4.toString()).thenReturn("4");
        Connection c5 = mock(Connection.class);
        when(c5.toString()).thenReturn("5");

        mockJDBCDriver.register("jdbc:four", c4);
        Connection nc4 = DriverManager.getConnection("jdbc:four");

        mockJDBCDriver.register("jdbc:five", c5);
        Connection nc5 = DriverManager.getConnection("jdbc:five");


        assertThat(nc4, hasToString("4"));
        assertThat(nc5, hasToString("5"));
    }

    @Test
    public void allowRegisterToMockTheConnection() throws SQLException {
        Connection c6 = mockJDBCDriver.register("jdbc:six");
        Connection c7 = mockJDBCDriver.register("jdbc:seven");
        when(c6.toString()).thenReturn("6");
        when(c7.toString()).thenReturn("7");

        Connection nc6 = DriverManager.getConnection("jdbc:six");
        Connection nc7 = DriverManager.getConnection("jdbc:seven");

        assertThat(nc6, hasToString("6"));
        assertThat(nc7, hasToString("7"));
    }

    @Test
    public void allowRegisterToMockTheConnection2() throws SQLException {
        Connection c6 = mockJDBCDriver.register("jdbc:six");
        when(c6.toString()).thenReturn("6.5");

        Connection nc6 = DriverManager.getConnection("jdbc:six");

        assertThat(nc6.toString(), equalTo("6.5"));
    }

    @Test
    public void UseTheMostDefaults() throws SQLException {
        Connection c = mockJDBCDriver.register();
        when(c.toString()).thenReturn("d");

        Connection nc = DriverManager.getConnection(MockJDBCDriver.MOCKURL);

        assertThat(nc, hasToString("d"));
    }

}
