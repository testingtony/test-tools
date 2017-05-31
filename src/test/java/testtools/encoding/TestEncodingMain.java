package testtools.encoding;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import testtools.encoding.EncodingMain;

import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class TestEncodingMain {
    EncodingMain main;
    CommandLineParser parser;


    @Before
    public void setup() {
        main = new EncodingMain();
        parser = new DefaultParser();
    }

    @Test
    public void testValidCommandsParsing() throws ParseException {
        String[] args = new String[]{"-g", "file"};

        CommandLine cmd = parser.parse(main.getCLIOptions(), args);
        String option = cmd.getOptionValue("g", null);

        assertThat(cmd.hasOption("g"), is(true));
        assertThat(option, is("file"));
        assertThat(cmd.getArgs(), arrayWithSize(0));
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testInvalidParameter() throws ParseException {
        String[] args = new String[]{"-y", "file"};

        thrown.expect(ParseException.class);
        thrown.expectMessage("Unrecognized option: -y");

        parser.parse(main.getCLIOptions(), args);
    }

    @Test
    public void testGuesserNeedsOnlyAFile() throws ParseException {
        String[] args = new String[]{"-g"};

        thrown.expect(ParseException.class);
        thrown.expectMessage("Missing argument for option: g");

        parser.parse(main.getCLIOptions(), args);
    }
}
