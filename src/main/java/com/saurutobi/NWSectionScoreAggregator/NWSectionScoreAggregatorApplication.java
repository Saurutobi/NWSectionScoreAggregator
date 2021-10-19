package com.saurutobi.NWSectionScoreAggregator;

import com.saurutobi.NWSectionScoreAggregator.Model.Participant;
import io.vavr.control.Option;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@SuppressWarnings("ThrowablePrintedToSystemOut")
@SpringBootApplication
public class NWSectionScoreAggregatorApplication {
    private static final String DELIMITER = "|";

    public static void main(String[] args) throws IOException {
        if (args.length == 0 || args[0].equals("-h") || args[0].equals("help")) {
            System.out.println("To import a match from The USPSA Classifier Report file");
            System.out.println("-i [input file path] [output file path]" + "\n");
            System.out.println("To aggregate all matches:");
            System.out.println("-a [input folder] [output file path]");
            System.exit(0);
        } else if (args[0].equals("-i")) {
            System.out.println("importing match");
            importMatch(args[1], args[2]);
        } else if (args[0].equals("-a")) {
            System.out.println("aggregating matches");
            //aggregate individual match into main collection
            //read directory
            //combine all
            //single csv output (or spreadsheet?)
            //final rows are shooters, columns are matches and match finish, ordered by match date jan-dec left-right (OR, dec-jan left-right)

        }
    }

    private static void importMatch(String inputFileName, String outputFileName) {
        Option.of(inputFileName).peek(inputFile ->
                                              Option.of(outputFileName).peek(outputFile -> {
                                                  final List<String> lines = readFile(inputFile);
                                                  final List<Participant> participants = filterParticipants(lines);
                                                  participants.sort(Comparator.comparing(Participant::getNameLast));
                                                  writeMatchReport(outputFile, participants);
                                              }));
    }

    private static void writeMatchReport(String outputFileName, List<Participant> participants) {
        try {
            final FileWriter myWriter = new FileWriter(outputFileName);
            writeMatchReportHeader(myWriter);
            for (Participant participant : participants) {
                String out = participant.toMatchReport(DELIMITER);
                System.out.println(out);
                myWriter.write(out + "\n");
            }
            myWriter.flush();
            myWriter.close();
        } catch (IOException e) {
            System.out.println("error writing file");
            System.out.println(e);
        }
    }

    private static void writeMatchReportHeader(FileWriter myWriter) throws IOException {
        myWriter.write("First|Last|USPSA Number|Division|Divison Finish|DQed" + "\n");
    }

    private static List<String> readFile(String fileName) {
        final ArrayList<String> lines = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("error reading file");
            System.out.println(e);
        }
        return lines;
    }

    private static List<Participant> filterParticipants(List<String> lines) {
        //raw line: "E 1, A122842, Jonathan, Tran, No, No, No, No, M, Carry Optics, 738.5825, 1, Minor,etcetc"
        return lines.stream()
                .map(line -> line.split(" "))
                .filter(elements -> elements[0].equals("E"))
                .map(NWSectionScoreAggregatorApplication::reduceDown)
                .map(line -> line.toLowerCase(Locale.ROOT))
                .map(line -> line.split(","))
                .map(Participant::mapParticipantFromFile)
                .collect(Collectors.toList());
    }

    private static String reduceDown(String[] things) {
        return Arrays.stream(things)
                .reduce("", (partialString, element) -> partialString + " " + element);
    }
}
