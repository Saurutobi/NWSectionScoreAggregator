package com.saurutobi.NWSectionScoreAggregator;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NWSectionScoreAggregatorApplication {
    public static void main(String[] args) {
        if (args.length == 0 || args[0].equals("-h") || args[0].equals("help")) {
            System.out.println("To import a single match from The USPSA Classifier Report file:");
            System.out.println("-i [input file path]" + "\n");
            System.out.println("To bulk import all matches from The USPSA Classifier Report file in a directory:");
            System.out.println("-ia  [input directory] [output directory]" + "\n");
            System.out.println("To QC matches in a directory:");
            System.out.println("-qc [input directory]" + "\n");
            System.out.println("Generate custom results from imported match:");
            System.out.println("-r [input file]" + "\n");
            System.out.println("To report on all matches in a directory:");
            System.out.println("-a [input folder] [output file path]" + "\n");
            System.out.println("To report on all DQs from all matches in a directory:");
            System.out.println("-d [input folder] [output file path]");
            System.out.println("To report on Section Series Awards:");
            System.out.println("-s [input folder] [output file path]");
            System.exit(0);
        } else if (args[0].equals("-i")) {
            System.out.println("Importing Match" + "\n");
            MatchImporter.importMatch(args[1], args[1] + ".json");
        } else if (args[0].equals("-ia")) {
            System.out.println("Bulk Importing Matches" + "\n");
            MatchImporter.importBulkMatches(args[1], args[2]);
        } else if (args[0].equals("-qc")) {
            System.out.println("Quality Checking Matches" + "\n");
            QualityChecker.qcMatches(args[1]);
        } else if (args[0].equals("-r")) {
            System.out.println("Generating Custom Results" + "\n");
            CustomMatchResultsGenerator.generateResultsFromMatch(args[1]);
        } else if (args[0].equals("-a")) {
            System.out.println("Aggregating Matches" + "\n");
            MatchAggregator.aggregateMatch(args[1], args[2], false);
        } else if (args[0].equals("-d")) {
            System.out.println("Aggregating Matches" + "\n");
            MatchAggregator.aggregateMatch(args[1], args[2], true);
        } else if(args[0].equals("-s")){
            System.out.println("Generation Section Series Matches" + "\n");
            SectionSeriesAggregator.createSeriesResults(args[1], args[2]);
        }
    }
}
