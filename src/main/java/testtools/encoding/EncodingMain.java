package testtools.encoding;


import org.apache.commons.cli.*;
import testtools.Tool;
import testtools.encoding.Converter;
import testtools.encoding.StreamGuesser;

import java.io.*;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;

public class EncodingMain implements Tool {

    @Override
    public Options getCLIOptions() {
        Options options = new Options();
        OptionGroup optionGroup = new OptionGroup();
        optionGroup.setRequired(true);


        optionGroup
                .addOption(Option.builder("g").longOpt("guess").hasArg().argName("file")
                        .desc("Guess the encoding of the file").build())
                .addOption(Option.builder("v").longOpt("validate").hasArg().numberOfArgs(2).argName("args")
                        .desc("Validate with the encoding - argumens: <encoding> <file>").build())
                .addOption(Option.builder("r").longOpt("recode").hasArg().numberOfArgs(4)
                        .argName("args").desc("Recode the file - arguments: input_encoding input_file output_encoding output_file").build()
                );
        options.addOptionGroup(optionGroup);

        return options;
    }

    @Override
    public String getCommand() {
        return "encoding";
    }

    @Override
    public String getArguments() {
        return "";
    }

    @Override
    public void runCommand(CommandLine commandLine) throws ParseException{
        try {
            if (commandLine.hasOption("g")) {
                guess(commandLine.getOptionValues("g"));
            }

            if (commandLine.hasOption("v")) {
                validate(commandLine.getOptionValues("v"));
            }

            if (commandLine.hasOption("r")) {
                recode(commandLine.getOptionValues("r"));
            }
        } catch (FileNotFoundException e) {
            throw new ParseException(e.getMessage());
        }
    }

    private void validate(String[] remainder) throws FileNotFoundException {
        String encodingName = remainder[0];
        String pathname = remainder[1];

        Charset encoding = Charset.forName(encodingName);
        InputStream source = new FileInputStream(new File(pathname));

        int result = Converter.validate(source, encoding);

        switch (result) {
            case 0:
                System.out.println("File " + pathname + " could be encoded with " + encodingName);
                break;

            case -1:
                System.out.println("Error occurred validating " + pathname);
                System.exit(2);

            default:
                System.out.println("File " + pathname + " is not valid for encoding " + encodingName);
                System.out.println("Error around character " + result);
                System.exit(1);
        }
    }

    private void recode(String[] options) throws FileNotFoundException {
        Charset inputEncoding = Charset.forName(options[0]);
        InputStream input = new FileInputStream(options[1]);
        Charset outputEncoding = Charset.forName(options[2]);
        OutputStream output = new FileOutputStream(options[3]);

        Converter c = new Converter(input, inputEncoding, output, outputEncoding);
        try {
            c.convert();
        } catch (CharacterCodingException e) {
            e.printStackTrace();
            System.out.println("Failed to recode after " + c.getCharsRead() + " characters");
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(2);
        }
    }

    private void guess(String[] options) throws FileNotFoundException {
        String pathname = options[0];
        InputStream source = new FileInputStream(new File(pathname));
        StreamGuesser guesser = new StreamGuesser(source);

        System.out.println("Guessing file " + pathname);
        guesser.guess();

        System.out.println("File encoding looks like " + guesser.getCharset());
        System.out.println("I am " + (guesser.confident() ? "reasonably" : "not very") + " confident of this.");
    }
}
