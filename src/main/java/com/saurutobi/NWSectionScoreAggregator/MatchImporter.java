package com.saurutobi.NWSectionScoreAggregator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saurutobi.NWSectionScoreAggregator.Model.Match;
import com.saurutobi.NWSectionScoreAggregator.Model.Participant;
import com.saurutobi.NWSectionScoreAggregator.Model.ParticipantStageResult;
import com.saurutobi.NWSectionScoreAggregator.Model.Stage;
import io.vavr.control.Option;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@SuppressWarnings("ThrowablePrintedToSystemOut")
public class MatchImporter {
    public static void importBulkMatches(String inputDirectory) {
        Option.of(inputDirectory).peek(inputFile -> {
            //find all files in the directory
            //for each file in files, importmatch(file, inputDirectoy+"json"+filename thing)

            throw new UnsupportedOperationException("This feature isn't built yet");
        });
    }

    public static void importMatch(String inputFileName) {
        Option.of(inputFileName).peek(inputFile -> {
            final List<String> lines = readFile(inputFile);
            final Match match = getBaseMatchInfo(lines);
            match.setStages(getStages(lines));
            match.setParticipants(getParticipants(lines));
            match.setParticipantStageResults(getStageResults(lines));
            writeMatchReport(inputFile, match);
        });
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

    private static Match getBaseMatchInfo(List<String> lines) {
        //raw line: "A Renton USPSA - May 2021 - NW05,asdf,05/23/2021"
        return lines.stream()
                .map(line -> line.split(" "))
                .filter(elements -> elements[0].equals("A"))
                .map(MatchImporter::reduceDown)
                .map(line -> line.split(","))
                .map(Match::mapBaseMatchInfoFromUSPSAMatchReportFile)
                .findFirst()
                .orElse(null);
    }

    private static List<Stage> getStages(List<String> lines) {
        //RAW attributes: "G 1,Pistol,18,90,No,08-03,Stage 1 - Dance Boatman Dance,Comstock,1"
        return lines.stream()
                .map(line -> line.split(" "))
                .filter(elements -> elements[0].equals("G"))
                .map(MatchImporter::reduceDown)
                .map(line -> line.split(","))
                .map(Stage::mapStageFromUSPSAMatchReportFile)
                .collect(Collectors.toList());
    }

    private static List<Participant> getParticipants(List<String> lines) {
        //raw line: "E 1, A1234, first, last, No, No, No, No, M, Carry Optics, 738.5825, 1, Minor,etcetc"
        return lines.stream()
                .map(line -> line.split(" "))
                .filter(elements -> elements[0].equals("E"))
                .map(MatchImporter::reduceDown)
                .map(line -> line.toLowerCase(Locale.ROOT))
                .map(line -> line.split(","))
                .map(Participant::mapParticipantFromUSPSAMatchReportFile)
                .collect(Collectors.toList());
    }

    private static List<ParticipantStageResult> getStageResults(List<String> lines) {
        //raw line: "I Pistol,1,1,No,No,13,0,5,0,0,0,0,2,0,0,0,0,0,0,0,14.56,0,0,0,0,14.56,80,80,5.4945,90.0000,1,"
        return lines.stream()
                .map(line -> line.split(" "))
                .filter(elements -> elements[0].equals("I"))
                .map(MatchImporter::reduceDown)
                .map(line -> line.split(","))
                .map(ParticipantStageResult::mapStageResultFromUSPSAMatchReportFile)
                .collect(Collectors.toList());
    }

    private static String reduceDown(String[] things) {
        return Arrays.stream(things)
                .reduce("", (partialString, element) -> partialString + " " + element);
    }

    private static void writeMatchReport(String inputFileName, Match match) {
        try {
            Util.getObjectMapper().writeValue(new File(inputFileName + ".json"), match);
        } catch (IOException e) {
            System.out.println("error reading file");
            System.out.println(e);
        }
    }
}
