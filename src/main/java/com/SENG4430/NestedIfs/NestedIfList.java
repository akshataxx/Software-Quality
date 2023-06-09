package com.SENG4430.NestedIfs;

import com.SENG4430.MetricsList;
import org.apache.commons.cli.*;
import spoon.Launcher;

import java.util.HashMap;
import java.util.Map;


public class NestedIfList extends MetricsList {
    private final NestedIfsChk nestedIfsChk;

    // Constructor
    public NestedIfList(String[] args) {
        final Options options = new Options();
        options.addOption("depth", true, "");

        final CommandLineParser parser = new DefaultParser();
        final CommandLine cmd = parseCommandLine(args, parser, options);

        // Parse the minimum depth from the command line arguments
        final int minDepth = parseMinDepth(cmd);

        // Create an instance of NestedIfsChk with the minimum depth
        nestedIfsChk = new NestedIfsChk(minDepth);
    }

    @Override
    public void execute(Launcher launcher) {
        // Execute the nested if checks using the provided Launcher
        nestedIfsChk.check(launcher);
    }

    @Override
    public String toJson() {
        // Initialize the JSON string with the Nested Ifs root object
        String json = "Nested Ifs: ";
        json += "\n\t Depth of nested ifs: ";
        for (Map.Entry<String, HashMap<String, Integer>> entry
                : nestedIfsChk.getNestedIfsScoresForClass().entrySet()) {
            json += "\n\t\tMinimum depth limit: " + nestedIfsChk.getNestedIfsLimit()
                    +"\n\t\tClass Name: "+ entry.getKey() + "\n\t\tDepth: " + entry.getValue();
        }
        json += "\n";
        // Return the complete JSON string
        return json;
    }

    // Parse the command line arguments
    private CommandLine parseCommandLine(String[] args, final CommandLineParser parser, final Options options) {
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return cmd;
    }

    // Parse the minimum depth value from the command line arguments
    private int parseMinDepth(final CommandLine cmd) {
        final String minDepthString = cmd.getOptionValue("depth");
        int minDepth = 3;
        if (minDepthString != null) {
            try {
                minDepth = Integer.parseInt(minDepthString);
            } catch (NumberFormatException e) {
                System.out.println("nested if depth value must be an integer");
                System.exit(1);
            }
        }
        return minDepth;
    }
}