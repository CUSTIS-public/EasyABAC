package custis.easyabac.model.xacmlconverter;

import org.apache.commons.cli.*;

import java.io.*;

public class ConverterRunner {
    public static void main(String[] args) {
        Option fromOption = Option.builder("from")
                .argName("path to file")
                .hasArg()
                .required()
                .type(File.class)
                .desc("EasyModel policy file name")
                .build();

        Option toOption = Option.builder("to")
                .argName("path to file")
                .hasArg()
                .required(false)
                .type(File.class)
                .desc("Target XACML file name. If missing, prints XACML to console")
                .build();

        Options options = new Options();
        options.addOption(fromOption);
        options.addOption(toOption);

        CommandLine cmd = null;
        try {
            CommandLineParser parser = new DefaultParser();
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.printf("Failed to parse command arguments: %s\n", e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java " + ConverterRunner.class.getName(), options);
            System.exit(-1);
        }

        String fromFileName = cmd.getOptionValue(fromOption.getOpt());
        File fromFile = new File(fromFileName);

        String xacml = null;
        try (FileReader reader = new FileReader(fromFile)) {
            xacml = new EasyModel2XACMLConverter().convert(reader);
        } catch (IOException e) {
            System.err.printf("Failed to read EasyModel file %s: %s\n", fromFileName, e.getMessage());
            System.exit(-1);
        } catch (ConversionException e) {
            System.err.printf("Failed to convert Easy model %s\n", e.getMessage());
            System.exit(-2);
        }

        if (cmd.hasOption(toOption.getOpt())) {
            try (FileWriter out = new FileWriter(toOption.getValue())) {
                out.write(xacml);
                out.flush();
            } catch (IOException e) {
                System.err.printf("Failed to write XACML to file %s: %s", toOption.getValue(), e.getMessage());
                System.exit(-3);
            }
        } else {
            System.out.printf("XACML:\n%s", xacml);
        }
    }
}