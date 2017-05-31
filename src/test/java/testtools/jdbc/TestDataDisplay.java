package testtools.jdbc;

import org.junit.Test;

import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

public class TestDataDisplay {

    @Test
    public void getLengthsWillCalculateFromMetaData() throws SQLException {
        ResultSet mockResultSet = mock(ResultSet.class);
        ResultSetMetaData mockMetaData = mock(ResultSetMetaData.class);
        when(mockResultSet.getMetaData()).thenReturn(mockMetaData);
        when(mockMetaData.getColumnCount()).thenReturn(3);
        mockMetaData(mockMetaData, 1, "a", 1);
        mockMetaData(mockMetaData, 2, "2^4^6^8^11^14^17^20*", 10);
        mockMetaData(mockMetaData, 3, "2^4*", 12);
        DataDisplay dataDisplay = new DataDisplay(mockResultSet);

        int[] lengths = dataDisplay.getLengths();

        assertThat(lengths, equalTo(new int[]{4, 20, 12}));
    }

    @Test
    public void getHeadersWillReturnNamesFromMetaData() throws SQLException {
        ResultSet mockResultSet = mock(ResultSet.class);
        ResultSetMetaData mockMetaData = mock(ResultSetMetaData.class);
        when(mockResultSet.getMetaData()).thenReturn(mockMetaData);
        when(mockMetaData.getColumnCount()).thenReturn(3);
        mockMetaData(mockMetaData, 1, "a", 1);
        mockMetaData(mockMetaData, 2, "2^4^6^8^11^14^17^20*", 10);
        mockMetaData(mockMetaData, 3, "2^4*", 12);
        DataDisplay dataDisplay = new DataDisplay(mockResultSet);

        String[] names = dataDisplay.getHeaders();

        assertThat(names, equalTo(new String[]{"a", "2^4^6^8^11^14^17^20*", "2^4*"}));
    }

    @Test
    public void getRowWillReturnCurrentRowFromResultSet() throws SQLException {
        ResultSet mockResultSet = mock(ResultSet.class);
        ResultSetMetaData mockMetaData = mock(ResultSetMetaData.class);
        when(mockResultSet.getMetaData()).thenReturn(mockMetaData);
        when(mockMetaData.getColumnCount()).thenReturn(3);
        when(mockResultSet.getString(1)).thenReturn("one");
        when(mockResultSet.getString(2)).thenReturn("two");
        when(mockResultSet.getString(3)).thenReturn("three");
        DataDisplay dataDisplay = new DataDisplay(mockResultSet);

        String[] rows = dataDisplay.getRow();

        assertThat(rows, equalTo(new String[]{"one", "two", "three"}));
    }

    @Test
    public void getRowWillHandleNULL() throws SQLException {
        ResultSet mockResultSet = mock(ResultSet.class);
        ResultSetMetaData mockMetaData = mock(ResultSetMetaData.class);
        when(mockResultSet.getMetaData()).thenReturn(mockMetaData);
        when(mockMetaData.getColumnCount()).thenReturn(1);
        when(mockResultSet.getString(1)).thenReturn("one");
        when(mockResultSet.wasNull()).thenReturn(true);
        DataDisplay dataDisplay = new DataDisplay(mockResultSet);

        String[] rows = dataDisplay.getRow();

        assertThat(rows, equalTo(new String[]{"NULL"}));
    }

    @Test
    public void prettyPrintWritesToPrintStream() throws SQLException {
        ResultSet mockResultSet = mock(ResultSet.class);
        ResultSetMetaData mockMetaData = mock(ResultSetMetaData.class);
        when(mockResultSet.getMetaData()).thenReturn(mockMetaData);
        when(mockMetaData.getColumnCount()).thenReturn(3);
        when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);

        DataDisplay dataDisplay = spy(new DataDisplay(mockResultSet));

        doReturn(new int[] {4, 10, 12}).when(dataDisplay).getLengths();
        doReturn(new String[] {"one", "two", "^3^5^7^9^12*"}).when(dataDisplay).getHeaders();
        doReturn(new String[] {"NULL", "^3^5^7^10*", "three"}).when(dataDisplay).getRow();

        PrintStream mockStream = mock(PrintStream.class);

        int rowCount = dataDisplay.prettyPrint(mockStream);

        verify(mockStream, times(6)).println(anyString());
        verify(mockStream, times(3)).println("+------+------------+--------------+");
        verify(mockStream, times(1)).println("| one  | two        | ^3^5^7^9^12* |");
        verify(mockStream, times(2)).println("| NULL | ^3^5^7^10* | three        |");
        assertThat(rowCount, equalTo(2));
    }

    private void mockMetaData(ResultSetMetaData metaData, int col, String name, int length) throws SQLException {
        when(metaData.getColumnLabel(col)).thenReturn(name);
        when(metaData.getColumnDisplaySize(col)).thenReturn(length);
    }
}
