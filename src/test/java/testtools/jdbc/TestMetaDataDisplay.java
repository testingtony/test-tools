package testtools.jdbc;

import org.junit.Test;

import java.io.PrintStream;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class TestMetaDataDisplay {

    @Test
    public void tinyColumnNamesLeaveRoomForTitle() throws SQLException {
        String[] colNames = {"a", "b", "c", "d"};
        ResultSetMetaData mockMeta = mock(ResultSetMetaData.class);
        when(mockMeta.getColumnCount()).thenReturn(colNames.length);
        for (int i = 0; i < colNames.length; i++) {
            when(mockMeta.getColumnLabel(i+1)).thenReturn(colNames[i]);
        }

        int[] results = new MetaDataDisplay(mockMeta).getLengths();

        assertThat(results, notNullValue());
        assertThat(results[0], equalTo("Name".length()));
    }

    @Test
    public void bigColumnNamesFindTheLargets() throws SQLException {
        String[] colNames = {"a", "^3^5^7^9^12*", "c"};
        ResultSetMetaData mockMeta = mock(ResultSetMetaData.class);
        when(mockMeta.getColumnCount()).thenReturn(colNames.length);
        for (int i = 0; i < colNames.length; i++) {
            when(mockMeta.getColumnLabel(i+1)).thenReturn(colNames[i]);
        }

        int[] results = new MetaDataDisplay(mockMeta).getLengths();

        assertThat(results, notNullValue());
        assertThat(results[0], equalTo(12));
    }

    @Test
    public void mutilRowMetaDataCreatesMutliRowTable() throws SQLException {
        String[] expected0 = {"^3*", "BIGINT", "12", "5", "3", "No",};
        String[] expected1 = {"^3^5*", "INTEGER", "0", "0", "0", "?",};
        ResultSetMetaData mockMeta = mock(ResultSetMetaData.class);
        when(mockMeta.getColumnCount()).thenReturn(2);
        createMockForMetadata(mockMeta, 1, expected0);
        createMockForMetadata(mockMeta, 2, expected1);

        String[][] results = new MetaDataDisplay(mockMeta).getTable();

        assertThat(results, notNullValue());
        assertThat(results, arrayWithSize(2));
        assertThat(results[0], notNullValue());
        assertThat(results[1], notNullValue());

        assertThat(results[0], equalTo(expected0));
        assertThat(results[1], equalTo(expected1));
    }


    @Test
    public void prettyPrint() throws SQLException {
        String[] expected0 = {"^3*", "BIGINT", "12", "5", "3", "No",};
        String[] expected1 = {"^3^5*", "INTEGER", "0", "0", "0", "?",};
        ResultSetMetaData mockMeta = mock(ResultSetMetaData.class);
        when(mockMeta.getColumnCount()).thenReturn(2);
        createMockForMetadata(mockMeta, 1, expected0);
        createMockForMetadata(mockMeta, 2, expected1);
        PrintStream mockStream = mock(PrintStream.class);

        new MetaDataDisplay(mockMeta).prettyPrint(mockStream);

        verify(mockStream, times(6)).println(anyString());
        verify(mockStream, times(3)).println("+-------+----------+------+-----------+-------+------+");
        verify(mockStream, times(1)).println("| name  | type     | size | precision | scale | null |");
        verify(mockStream, times(1)).println("| ^3*   | BIGINT   | 12   | 5         | 3     | No   |");
        verify(mockStream, times(1)).println("| ^3^5* | INTEGER  | 0    | 0         | 0     | ?    |");
    }


    private void createMockForMetadata(ResultSetMetaData mockMeta, int column, String[] wanted) throws SQLException {
        when(mockMeta.getColumnLabel(column)).thenReturn(wanted[0]);
        if ("BIGINT".equals(wanted[1])) {
            when(mockMeta.getColumnType(column)).thenReturn(Types.BIGINT);
        } else if ("INTEGER".equals(wanted[1])) {
            when(mockMeta.getColumnType(column)).thenReturn(Types.INTEGER);
        } else {
            when(mockMeta.getColumnType(column)).thenReturn(Types.VARCHAR);
        }
        when(mockMeta.getColumnDisplaySize(column)).thenReturn(Integer.valueOf(wanted[2]));
        when(mockMeta.getPrecision(column)).thenReturn(Integer.valueOf(wanted[3]));
        when(mockMeta.getScale(column)).thenReturn(Integer.valueOf(wanted[4]));

        if ("No".equals(wanted[5])) {
            when(mockMeta.isNullable(column)).thenReturn(ResultSetMetaData.columnNoNulls);
        } else if ("Yes".equals(wanted[5])) {
            when(mockMeta.isNullable(column)).thenReturn(ResultSetMetaData.columnNullable);
        } else {
            when(mockMeta.isNullable(column)).thenReturn(ResultSetMetaData.columnNullableUnknown);
        }
    }

}
