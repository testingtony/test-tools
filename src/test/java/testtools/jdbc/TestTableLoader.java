package testtools.jdbc;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestTableLoader {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void buildQueryRejectsNullTableName() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage(containsString("table"));

        TableLoader.buildQuery(null, new String[0]);
    }

    @Test
    public void buildQueryRejectsNullFields() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage(containsString("fields"));

        TableLoader.buildQuery("fred", null);

    }

    @Test
    public void simpleQueryBuild() {
        String tableName = "tableName";
        String [] fields = {"f1", "f2"};
        String expectedQuery = "INSERT INTO tableName(f1, f2) VALUES (?, ?)";

        String result = TableLoader.buildQuery(tableName, fields);

        assertThat(result, equalTo(expectedQuery));
    }

    @Test
    public void longerQueryBuild() {
        String tableName = "tableName";
        String [] fields = {"f1", "f2", "f3"};
        String expectedQuery = "INSERT INTO tableName(f1, f2, f3) VALUES (?, ?, ?)";

        String result = TableLoader.buildQuery(tableName, fields);

        assertThat(result, equalTo(expectedQuery));
    }
}
