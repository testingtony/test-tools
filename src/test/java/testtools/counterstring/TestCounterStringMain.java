package testtools.counterstring;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import testtools.TestToolsMain;
import testtools.counterstring.CounterStringMain;

import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class TestCounterStringMain {
    CounterStringMain main;
    CommandLineParser parser;

    @Before
    public void setup() {
        main = new CounterStringMain();
        parser = new DefaultParser();
    }

    @Test
    public void testValidCommandsParsing() throws ParseException {
        String[] args = new String[]{"-s", "x", "-t", "y", "500"};

        CommandLine cmd = TestToolsMain.getCommandLine(new CounterStringMain(), args);

        assertThat(cmd.getOptionValue("s"), is("x"));
        assertThat(cmd.getOptionValue("t"), is("y"));
        assertThat(cmd.getArgs(), arrayWithSize(1));
        assertThat(cmd.getArgs()[0], is("500"));

    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testInvalidCommandsThrowError() throws ParseException {
        String[] args = new String[]{"-a", "x", "-t", "y"};

        thrown.expect(ParseException.class);
        thrown.expectMessage("-a");

        parser.parse(main.getCLIOptions(), args);
    }

    @Test
    public void testCounterTerminatorArgsAreOnlyChars() throws ParseException {
        String[] badT = new String[]{"-t", "justone"};

        thrown.expect(ParseException.class);
        thrown.expectMessage("single char");

        CommandLine cmd = parser.parse(main.getCLIOptions(), badT);
        main.validateCommandLine(cmd);
    }

    @Test
    public void testCounterSeparatorArgsAreOnlyChars() throws ParseException {
        String[] badS = new String[]{"counterstring", "-s", "justone"};

        thrown.expect(ParseException.class);
        thrown.expectMessage("single char");

        CommandLine cmd = parser.parse(main.getCLIOptions() , badS);
        main.validateCommandLine(cmd);
    }
}
