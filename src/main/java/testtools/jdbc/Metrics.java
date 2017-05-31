package testtools.jdbc;

import java.security.InvalidParameterException;

/*
 * Do the borders around a table.
 */
public class Metrics {
    private int [] lengths;

    private final static String VBAR = "|";
    private final static String HBAR = "-";
    private final static String CROSS = "+";

    public Metrics(int[] lengths) {
        this.lengths = lengths;
    }

    public String getSeparator(){
        StringBuilder result = new StringBuilder();
        result.append(CROSS);
        for(int length: lengths) {
            for(int i = 0; i < length + 2; i++) {
                result.append(HBAR);
            }
            result.append(CROSS);
        }
        return result.toString();
    }

    public String getRow(String[] values) {
        int columnLength = lengths.length;
        int valuesLength = values.length;
        if (columnLength != valuesLength) {
            throw new InvalidParameterException("Expected " + columnLength + " columns, but got " + valuesLength);
        }

        StringBuilder result = new StringBuilder();
        result.append(VBAR);
        for(int column = 0; column < columnLength; column++) {
            result.append(' ');
            result.append(values[column]);
            for(int i=0; i < lengths[column] - values[column].length(); i++) {
                result.append(' ');
            }
            result.append(' ');
            result.append(VBAR);
        }
        return result.toString();
    }

}
