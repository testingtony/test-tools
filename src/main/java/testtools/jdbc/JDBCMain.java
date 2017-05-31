package testtools.jdbc;

import com.opencsv.CSVWriter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import testtools.Tool;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCMain implements Tool {

    @Override
    public Options getCLIOptions() {
        Options options;
        OptionGroup functions = new OptionGroup()
                .addOption(Option.builder("d").longOpt("describe").desc("Describe the table layout").build())
                .addOption(Option.builder("D").longOpt("dump").desc("Dump the table to the screen").build())
                .addOption(Option.builder("w").longOpt("write").hasArg().argName("filename").desc("Write the table to a file").build())
                .addOption(Option.builder("r").longOpt("read").hasArg().argName("filename").desc("Inserts into a table by reading csv").build())
                .addOption(Option.builder("s").longOpt("sql").desc("Executes SQL").build())
        ;
        functions.setRequired(true);

        options = new Options()
                .addOptionGroup(functions)
                .addOption(Option.builder("u").longOpt("user").hasArg().argName("user").desc("user").build())
                .addOption(Option.builder("p").longOpt("pass").hasArg().argName("password").desc("password").build())
        ;
        return options;
    }


    @Override
    public String getCommand() {
        return "jdbc";
    }

    @Override
    public String getArguments() {
        return "url [table|sql]";
    }

    @Override
    public void runCommand(CommandLine commandLine) {
        String[] remainder = commandLine.getArgs();

        String user = commandLine.getOptionValue("u", "");
        String password = commandLine.getOptionValue("p", "");

        if (commandLine.hasOption("d")) {
            describeTable(user, password, remainder);
        }

        if (commandLine.hasOption("D")) {
            dumpTable(user, password, remainder);
        }

        if (commandLine.hasOption("w")) {
            String filename = commandLine.getOptionValue("w", "-");
            writeTable(user, password, filename, remainder);
        }

        if (commandLine.hasOption("r")) {
            String filename = commandLine.getOptionValue("r", "-");
            readTable(user, password, filename, remainder);
        }

        if (commandLine.hasOption("s")) {
            execSQL(user, password, remainder);
        }
    }

    public void describeTable(String user, String password, String [] remainder) {
        String url = remainder[0];
        String table = remainder[1];

        try (
                ConnectionManager connectionManager = new ConnectionManager(user, password, url);
                ResultSet resultSet = connectionManager.executeQuery("select * from " + table + " where 1 = 2");
        ) {
            MetaDataDisplay metaDataDisplay = new MetaDataDisplay(resultSet.getMetaData());
            metaDataDisplay.prettyPrint(System.out);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void dumpTable(String user, String password, String [] remainder) {
        String url = remainder[0];
        String table = remainder[1];

        try (
                ConnectionManager connectionManager = new ConnectionManager(user, password, url);
                ResultSet resultSet = connectionManager.executeQuery("select * from " + table);
        ) {
            DataDisplay dataDisplay = new DataDisplay(resultSet);
            dataDisplay.prettyPrint(System.out);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void writeTable(String user, String password, String file, String [] remainder) {
        String url = remainder[0];
        String sql = remainder[1];

        try (
                ConnectionManager connectionManager = new ConnectionManager(user, password, url);
                ResultSet resultSet = connectionManager.executeQuery(sql);
                Writer writer = "-".equals(file) ? new OutputStreamWriter(System.out) : new FileWriter(file);
        ) {
            CSVWriter CsvWriter = new CSVWriter(writer);
            CsvWriter.writeAll(resultSet, true);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readTable(String user, String password, String file, String [] remainder) {
        String url = remainder[0];
        String table = remainder[1];

        try (
                ConnectionManager connectionManager = new ConnectionManager(user, password, url);
                Reader reader = "-".equals(file) ? new InputStreamReader(System.in) : new FileReader(file);
        ) {
            TableLoader tableLoader = new TableLoader(connectionManager, table);
            connectionManager.turnOffAutoCommit();
            tableLoader.read(reader);
            connectionManager.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void execSQL(String user, String password, String [] remainder) {
        String url = remainder[0];
        String sql = remainder[1];

        try (
                ConnectionManager connectionManager = new ConnectionManager(user, password, url);
                Statement statement = connectionManager.createStatement();
        ) {

                boolean isResultSet = statement.execute(sql);
                int records = statement.getUpdateCount();

                do {
                    if (isResultSet) {
                        ResultSet resultSet = statement.getResultSet();
                        DataDisplay dataDisplay = new DataDisplay(resultSet);
                        int count = dataDisplay.prettyPrint(System.out);
                        System.out.println();
                        System.out.println("" + count + " rows read");
                        System.out.println();
                    }
                    if (records != -1) {
                        System.out.println();
                        System.out.println("" + records + " rows affected");
                        System.out.println();
                    }

                    isResultSet = statement.getMoreResults();
                    records = statement.getUpdateCount();
                } while (isResultSet == true || records > -1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
