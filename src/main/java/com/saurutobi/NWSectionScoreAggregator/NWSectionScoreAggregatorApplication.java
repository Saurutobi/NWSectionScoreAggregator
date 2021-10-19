package com.saurutobi.NWSectionScoreAggregator;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class NWSectionScoreAggregatorApplication {
    public static void main(String[] args) {
        if (args.length == 0 || args[0].equals("-h") || args[0].equals("help")) {
            System.out.println("To import a match from The USPSA Classifier Report file");
            System.out.println("-i [input file path] [output file path]" + "\n");
            System.out.println("To aggregate all matches from the Section:");
            System.out.println("-a [input folder] [output file path]");
            System.exit(0);
        } else if (args[0].equals("-i")) {
            System.out.println("Importing Match" + "\n");
            MatchImporter.importMatch(args[1], args[2]);
        } else if (args[0].equals("-a")) {
            System.out.println("Aggregating Section Matches" + "\n");
            SectionAggregator.aggregateMatch(args[1], args[2]);
        }
    }
}
