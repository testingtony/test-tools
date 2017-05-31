package testtools.jdbc;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import static java.sql.Types.*;

public class TableLoader {
    private final ConnectionManager connectionManager;
    private final String table;
    private int [] types;


    public TableLoader(ConnectionManager connectionManager, String table) throws SQLException {
        this.connectionManager = connectionManager;
        this.table = table;
    }

    public Map<String, Integer> getFieldsType(){
        Map<String, Integer> columnIdToType = new HashMap<>();
        try (ResultSet resultSet = connectionManager.executeQuery("SELECT * FROM " + table + " WHERE 1 = 2")) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columns = metaData.getColumnCount();

            for (int col = 1; col <= columns; col++) {
                columnIdToType.put(metaData.getColumnLabel(col), metaData.getColumnType(col));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return columnIdToType;
    }

    public int [] getFieldTypes(String [] fields) {
        int [] types = new int[fields.length];
        Map<String, Integer> fieldTypes = getFieldsType();
        for(int i = 0; i < fields.length; i++) {
            types[i] = fieldTypes.get(fields[i]);
        }
        return types;
    }

    public static String buildQuery(String table, String [] fields) {
        if (table == null) {
            throw new NullPointerException("table name should not be null");
        }
        if (fields == null) {
            throw new NullPointerException("fields should not be null");
        }

        StringBuilder insert = new StringBuilder();
        StringBuilder values = new StringBuilder();

        insert.append("INSERT INTO ");
        insert.append(table);
        String sep = "(";
        for(String field: fields) {
            insert.append(sep);
            insert.append(field);
            values.append(sep);
            values.append("?");
            sep = ", ";
        }
        insert.append(") VALUES ");
        values.append(")");
        insert.append(values);

        return insert.toString();
    }

    /*
    Load table from csv

    create reader
    create csvreader

    get first row as columns
    create query from columns;

    create preparedStatement from query

    while csvrows
       fill in preparedStatement
       commit

    statement close
     */

    public void read(Reader reader) throws IOException, SQLException {
        CSVReader csvReader = new CSVReaderBuilder(reader)
                .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS)
                .build();
        String [] fields = csvReader.readNext();
        types = getFieldTypes(fields);

        String queryString = buildQuery(table, fields);

        PreparedStatement preparedStatement = connectionManager.prepareStatement(queryString);

        for(String [] row: csvReader){
            for (int i = 0 ; i < row.length; i++) {
                int fieldType = types[i];
                if (row[i] != null) {
                    preparedStatement.setObject(i + 1, translate(fieldType, row[i]));
                } else {
                    preparedStatement.setNull(i + 1, fieldType);
                }
            }
            preparedStatement.executeUpdate();
        }
    }


    public static Object translate(int type, String stringValue) throws SQLException{
        // https://www.cis.upenn.edu/~bcpierce/courses/629/jdkdocs/guide/jdbc/getstart/mapping.doc.html

        Object value = null;
        switch(type) {
            case BINARY:
            case VARBINARY:
            case LONGVARBINARY:
                throw new SQLDataException("cannot convert from BINARY, VARBINARY or LONGBINARY");
                // break;
            case CHAR:
            case VARCHAR:
            case LONGVARCHAR:
                value = stringValue;
                break;
            case NUMERIC:
            case DECIMAL:
                value = new BigDecimal(stringValue);
                break;
            case BIT:
                value = Boolean.valueOf(stringValue);
                break;
            case TINYINT:
            case SMALLINT:
            case INTEGER:
                value = Integer.valueOf(stringValue);
                break;
            case BIGINT:
                value = Long.valueOf(stringValue);
                break;
            case REAL:
                value = Float.valueOf(stringValue);
                break;
            case FLOAT:
            case DOUBLE:
                value = Double.valueOf(stringValue);
                break;
            case DATE:
                value = java.sql.Date.valueOf(stringValue);
                break;
            case TIME:
                value = java.sql.Time.valueOf(stringValue);
                break;
            case TIMESTAMP:
                value = java.sql.Timestamp.valueOf(stringValue);
                break;
        }
        return value;
    }
}
