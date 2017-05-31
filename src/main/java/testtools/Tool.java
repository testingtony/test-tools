package testtools;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Anything which runs a tool should implement this.
 */
public interface Tool {
    public Options getCLIOptions();
    public String getCommand();
    public String getArguments();
    public void runCommand(CommandLine commandLine) throws ParseException;
}
