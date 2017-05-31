package testtools.jdbc;

import org.apache.commons.cli.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestJDBCMain {
    private JDBCMain main;
    private CommandLineParser parser;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        main = new JDBCMain();
        parser = new DefaultParser();
    }

    @Test
    public void mustSpecifyAtLeastOneOption() throws ParseException {
        String [] args = new String [] {};

        thrown.expect(ParseException.class);
        thrown.expectMessage("Missing required option");

        parser.parse(main.getCLIOptions(), args);
    }

    @Test
    public void handlesLittleDOption() {
        Option o = main.getCLIOptions().getOption("d");

        assertThat(o, notNullValue());
        assertThat(o.getLongOpt(), equalTo("describe"));
        assertThat(o.hasArg(), is(false));
    }

    @Test
    public void handlesBigDOption() {
        Option o = main.getCLIOptions().getOption("D");

        assertThat(o, notNullValue());
        assertThat(o.getLongOpt(), equalTo("dump"));
        assertThat(o.hasArg(), is(false));
    }

    @Test
    public void handlesWOption() {
        Option o = main.getCLIOptions().getOption("w");

        assertThat(o, notNullValue());
        assertThat(o.getLongOpt(), equalTo("write"));
        assertThat(o.hasArg(), is(true));
    }

    @Test
    public void handlesROption() {
        Option o = main.getCLIOptions().getOption("r");

        assertThat(o, notNullValue());
        assertThat(o.getLongOpt(), equalTo("read"));
        assertThat(o.hasArg(), is(true));
    }

    @Test
    public void handleseOption() {
        Option o = main.getCLIOptions().getOption("s");

        assertThat(o, notNullValue());
        assertThat(o.getLongOpt(), equalTo("sql"));
        assertThat(o.hasArg(), is(false));
    }

    @Test
    public void handlesUserOption() {
        Option o = main.getCLIOptions().getOption("u");

        assertThat(o, notNullValue());
        assertThat(o.getLongOpt(), equalTo("user"));
        assertThat(o.hasArg(), is(true));
    }

    @Test
    public void handlesUserPassOption() {
        Option o = main.getCLIOptions().getOption("p");

        assertThat(o, notNullValue());
        assertThat(o.getLongOpt(), equalTo("pass"));
        assertThat(o.hasArg(), is(true));
    }
}
