package testtools.jdbc;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

public class MetaDataDisplay {

    public final static String NAME = "name";
    public final static String TYPE = "type";
    public final static String SIZE = "size";
    public final static String PRECISION = "precision";
    public final static String SCALE = "scale";
    public final static String NULL = "null";

    public final static String[] headers = {NAME, TYPE, SIZE, PRECISION, SCALE, NULL};

    private static final Map<Integer, String> dbTypeNames = new HashMap<>();

    static {
        for (Field field : Types.class.getFields()) {
            try {
                dbTypeNames.put((Integer) field.get(null), field.getName());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    ResultSetMetaData metadata;

    public MetaDataDisplay(ResultSetMetaData metadata) {
        this.metadata = metadata;
    }

    public int[] getLengths() {
        int maxColumnLabelLength = NAME.length();
        try {
            int columns = metadata.getColumnCount();
            for (int i = 1; i <= columns; i++) {
                int columnLabelLength = metadata.getColumnLabel(i).length();
                if (columnLabelLength > maxColumnLabelLength) {
                    maxColumnLabelLength = columnLabelLength;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new int[]{
                maxColumnLabelLength, // name
                8, // type
                4, // size
                9, // precision
                5, // scale
                4, // null
        };
    }

    public String[][] getTable() {
        try {
            int numberColumns = metadata.getColumnCount();
            String[][] results = new String[numberColumns][];

            for (int col = 1; col <= numberColumns; col++) {
                String[] columns = new String[6];
                columns[0] = metadata.getColumnLabel(col);
                columns[1] = dbTypeNames.get(metadata.getColumnType(col));
                columns[2] = Integer.valueOf(metadata.getColumnDisplaySize(col)).toString();
                columns[3] = Integer.valueOf(metadata.getPrecision(col)).toString();
                columns[4] = Integer.valueOf(metadata.getScale(col)).toString();
                int nullable = metadata.isNullable(col);
                columns[5] = (nullable == ResultSetMetaData.columnNoNulls ? "No" :
                        (nullable == ResultSetMetaData.columnNullable ? "Yes" : "?"));
                results[col -1] = columns;
            }
            return results;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void prettyPrint(PrintStream stream) throws SQLException{
        Metrics metrics = new Metrics(getLengths());

        stream.println(metrics.getSeparator());
        stream.println(metrics.getRow(headers));
        stream.println(metrics.getSeparator());

        String[][] table = getTable();
        for (String[] row: table) {
            stream.println(metrics.getRow(row));
        }
        stream.println(metrics.getSeparator());

    }
}
