package testtools.jdbc;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

public class TestMetrics {
    int[] widths = new int[] { 10, 15, 12};
    private Metrics metrics = new Metrics(widths);

    @Test
    public void createsTheRightSeparator() {
        //                           .^3^5^7^10*. .^3^5^7^9^12^15*. .^3^5^7^9^12*.
        String expectedSeparator = "+------------+-----------------+--------------+";

        String separator = metrics.getSeparator();

        assertThat(separator, equalTo(expectedSeparator));
    }

    @Test
    public void createsTheRightRow() {
        String[] goodRow = new String[] {"one", "two", "three"};
        //                          .^3^5^7^10*. .^3^5^7^9^12^15*. .^3^5^7^9^12*.
        String exepctedGoodRows = "| one        | two             | three        |";

        String row = metrics.getRow(goodRow);

        assertThat(row, equalTo(exepctedGoodRows));
    }

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Test
    public void invalidRowLengthThrowsAnException() {
        String [] badRow = new String [] {"one", "two"};

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("2"));
        thrown.expectMessage(containsString("3"));

        String row = metrics.getRow(badRow);
    }


}
