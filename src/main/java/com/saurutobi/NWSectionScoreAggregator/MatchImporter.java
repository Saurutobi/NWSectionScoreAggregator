package com.saurutobi.NWSectionScoreAggregator;

import com.saurutobi.NWSectionScoreAggregator.Model.ParticipantMatchAttendance;
import io.vavr.control.Option;

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
public class MatchImporter {
    private static final String DELIMITER = "|";

    public static void importBulkMatches(String inputDirectory, String outputDirectory) {
        Option.of(inputDirectory).peek(inputFile ->
                                               Option.of(outputDirectory).peek(outputFile -> {
                                                   //find all files in the directory
                                                   //for each file in files, importmatch(file, outputDirectory+filename thing)

                                                   throw new UnsupportedOperationException("This feature isn't built yet");
                                               }));
    }

    public static void importMatch(String inputFileName, String outputFileName) {
        Option.of(inputFileName).peek(inputFile ->
                                              Option.of(outputFileName).peek(outputFile -> {
                                                  final List<String> lines = readFile(inputFile);
                                                  final List<ParticipantMatchAttendance> participantMatchAttendances = filterParticipants(lines);
                                                  participantMatchAttendances.sort(Comparator.comparing(p -> p.getParticipant().getNameLast()));
                                                  writeMatchReport(outputFile, participantMatchAttendances);
                                              }));
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

    private static List<ParticipantMatchAttendance> filterParticipants(List<String> lines) {
        //raw line: "E 1, A122842, Jonathan, Tran, No, No, No, No, M, Carry Optics, 738.5825, 1, Minor,etcetc"
        return lines.stream()
                .map(line -> line.split(" "))
                .filter(elements -> elements[0].equals("E"))
                .map(MatchImporter::reduceDown)
                .map(line -> line.toLowerCase(Locale.ROOT))
                .map(line -> line.split(","))
                .map(ParticipantMatchAttendance::mapParticipantFromUSPSAMatchReportFile)
                .collect(Collectors.toList());
    }

    private static String reduceDown(String[] things) {
        return Arrays.stream(things)
                .reduce("", (partialString, element) -> partialString + " " + element);
    }

    private static void writeMatchReport(String outputFileName, List<ParticipantMatchAttendance> participantMatchAttendances) {
        try {
            final FileWriter myWriter = new FileWriter(outputFileName);
            writeMatchReportHeader(myWriter);
            for (ParticipantMatchAttendance participantMatchAttendance : participantMatchAttendances) {
                String out = participantMatchAttendance.toMatchReport(DELIMITER);
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
}
