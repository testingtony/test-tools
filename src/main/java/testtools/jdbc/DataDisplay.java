package testtools.jdbc;

import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class DataDisplay {
    public static final String NULLLABEL = "NULL";
    private ResultSet resultSet;
    private ResultSetMetaData metaData;
    private int columns;

    public DataDisplay(ResultSet resultSet) {
        this.resultSet = resultSet;
        try {
            metaData = resultSet.getMetaData();
            columns = metaData.getColumnCount();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int[] getLengths() {
        int[] lengths = new int[columns];
        try {
            for (int i = 1; i <= columns; i++) {
                int maxlen = NULLLABEL.length();
                maxlen = Math.max(maxlen, metaData.getColumnLabel(i).length());
                maxlen = Math.max(maxlen, metaData.getColumnDisplaySize(i));
                lengths[i - 1] = maxlen;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lengths;
    }

    public String[] getHeaders() {
        String [] headers = new String[columns];
        try {
            for (int i = 1; i <= columns; i++) {
                headers[i - 1] = metaData.getColumnLabel(i);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return headers;

    }

    public String[] getRow() {
        String [] row = new String[columns];
        try {
            for(int i = 1; i<= columns; i++ ) {
                row[i - 1] = resultSet.getString(i);
                if (resultSet.wasNull()) {
                    row[i-1] = NULLLABEL;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return row;
    }

    public int prettyPrint(PrintStream stream) throws SQLException{
        int rowCount = 0;
        Metrics metrics = new Metrics(getLengths());

        stream.println(metrics.getSeparator());
        stream.println(metrics.getRow(getHeaders()));
        stream.println(metrics.getSeparator());
        while(resultSet.next()) {
            stream.println(metrics.getRow(getRow()));
            rowCount++;
        }
        stream.println(metrics.getSeparator());
        return rowCount;
    }
}
