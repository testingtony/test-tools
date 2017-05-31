package testtools.counterstring;


import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import testtools.Tool;
import testtools.counterstring.CounterString;

public class CounterStringMain implements Tool {

    @Override
    public Options getCLIOptions(){
        Options options = new Options();
        options.addOption(Option.builder("t").longOpt("terminator").hasArg().argName("terminator").desc("Terminator for Counter Strings").build())
                .addOption(Option.builder("s").longOpt("separator").hasArg().argName("separator").desc("Separator for Counter Strings").build())
                .addOption(Option.builder("n").desc("no carriage return at end of string").build());
        return options;
    }

    @Override
    public String getCommand() {
        return "counterstring";
    }

    @Override
    public String getArguments() {
        return "length";
    }

    @Override
    public void runCommand(CommandLine commandLine) throws ParseException {
        validateCommandLine(commandLine);


        String [] remainder = commandLine.getArgs();

        String terminator = commandLine.getOptionValue("t", "*");
        String separator = commandLine.getOptionValue("s", "^");
        boolean noCr = commandLine.hasOption("n");
        String numberAsString;
        int number;

        try {
            numberAsString = remainder[0];
            number = Integer.parseInt(numberAsString);
        } catch (IndexOutOfBoundsException e) {
            throw new ParseException("counter string takes the length as the argument");
        } catch (NumberFormatException e) {
            throw new ParseException("counter string requires a number for the length");
        }

        CounterString generator = new CounterString(separator.charAt(0), terminator.charAt(0));
        String counterString = generator.generate(number);
        if (noCr) {
            System.out.print(counterString);
        } else {
            System.out.println(counterString);
        }
    }

    void validateCommandLine(CommandLine commandLine) throws ParseException {
        if (commandLine.hasOption("t")) {
            String val = commandLine.getOptionValue("t");
            if (val.length() != 1) {
                throw new ParseException("terminator option can only be a single char");
            }
        }
        if (commandLine.hasOption("s")) {
            String val = commandLine.getOptionValue("s");
            if (val.length() != 1) {
                throw new ParseException("separator option can only be a single char");
            }
        }
    }

}
