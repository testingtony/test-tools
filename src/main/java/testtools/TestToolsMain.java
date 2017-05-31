package testtools;

import org.apache.commons.cli.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.ServiceLoader;

public class TestToolsMain {
    private final static ServiceLoader<Tool> tools = ServiceLoader.load(Tool.class);

    public static void main(String[] args) {
        if (args.length == 0) {
            showCommands("No command passed");
        }

        String command = args[0];
        String[] options = Arrays.copyOfRange(args, 1, args.length);

        boolean found = false;
        for (Tool tool : tools) {
            if (tool.getCommand().equalsIgnoreCase(command)) {
                CommandLine cmd = getCommandLine(tool, options);
                run(tool, cmd);
                found = true;
            }
        }

        if (!found) {
            showCommands(command + " not known");
        }
    }

    public static void run(Tool tool, CommandLine cmd){
        try {
            tool.runCommand(cmd);
        } catch (ParseException e) {
            System.err.println(e.getLocalizedMessage());
            printHelp(tool);
        }
    }

    public static CommandLine getCommandLine(Tool tool, String [] args) {
        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(tool.getCLIOptions(), args);
            return cmd;
        } catch (ParseException e) {
            if ("Unrecognized option: -h".equals(e.getMessage()) ||
                    "Unrecognized option: --help".equals(e.getMessage()) ) {
                printHelp(tool);
            } else {
                System.err.println(e.getLocalizedMessage());
                System.err.println("usage: " + getUsage(tool));
                System.err.println("help: " + tool.getCommand() + " -h");
            }
            System.exit(1);
        }
        return null;
    }
    
    private static String getUsage(Tool tool) {
        StringWriter out = new StringWriter();
        HelpFormatter formatter = new HelpFormatter();
        formatter.setSyntaxPrefix("");
        formatter.printUsage(new PrintWriter(out, true), formatter.getWidth(),
                tool.getCommand(), tool.getCLIOptions());
        return out.toString().trim() + " " + tool.getArguments();
    }

    public static void printHelp(Tool tool) {
        StringWriter out = new StringWriter();
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(getUsage(tool), tool.getCLIOptions());
    }


    private static void showCommands(String message) {
        System.err.println(message);
        System.err.println("Known commands");
        for (Tool tool : tools) {
            System.out.println(getUsage(tool));
        }

        System.exit(1);
    }
}
