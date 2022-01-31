package com.saurutobi.NWSectionScoreAggregator;

import com.saurutobi.NWSectionScoreAggregator.Model.Match;
import com.saurutobi.NWSectionScoreAggregator.Model.Participant;
import com.saurutobi.NWSectionScoreAggregator.Model.ParticipantStageResult;
import com.saurutobi.NWSectionScoreAggregator.Model.Stage;
import io.vavr.control.Option;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@SuppressWarnings("ThrowablePrintedToSystemOut")
public class MatchImporter {
    private static final String INFO_LINE_MARKER = "$INFO ";
    private static final String STAGE_LINE_MARKER = "G ";
    private static final String PARTICIPANT_LINE_MARKER = "E ";
    private static final String STAGE_RESULT_LINE_MARKER = "I ";

    public static void importBulkMatches(String inputDirectory, String outputDirectory) {
        Option.of(inputDirectory).peek(inputDir ->
                                               Option.of(outputDirectory).peek(outputDir ->
                                                                                       FileUtils.listFiles(new File(inputDir), null, true).forEach(file -> {
                                                                                           final String outputPath = outputDir + "\\" + file.getName() + ".json";
                                                                                           //noinspection ResultOfMethodCallIgnored
                                                                                           new File(outputDir).mkdirs();
                                                                                           System.out.println("Importing " + file.getName());
                                                                                           importMatch(file.getAbsolutePath(), outputPath);
                                                                                       })));
    }

    public static void importMatch(String inputFilePath, String outputFilePath) {
        Option.of(inputFilePath).peek(inputFile ->
                                              Option.of(outputFilePath).peek(outputFile -> {
                                                  final List<String> lines = readFile(inputFile);
                                                  final Match match = getBaseMatchInfo(lines);
                                                  match.setStages(getStages(lines));
                                                  match.setParticipants(getParticipants(lines));
                                                  match.setParticipantStageResults(getStageResults(lines));
                                                  writeMatchReport(outputFile, match);
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

    private static Match getBaseMatchInfo(List<String> lines) {
        //raw line: "A MatchName with commas possibly,asdf,05/23/2021"
        final String matchName = lines.stream()
                .filter(line -> line.startsWith(INFO_LINE_MARKER))
                .filter(line -> line.contains("Match name"))
                .map(line -> line.split(":")[1])
                .findFirst()
                .orElse(null);
        final String matchDate = lines.stream()
                .filter(line -> line.startsWith(INFO_LINE_MARKER))
                .filter(line -> line.contains("Match date"))
                .map(line -> line.split(":")[1])
                .findFirst()
                .orElse(null);
        ;
        final String clubName = lines.stream()
                .filter(line -> line.startsWith(INFO_LINE_MARKER))
                .filter(line -> line.contains("Club Name"))
                .map(line -> line.split(":")[1])
                .findFirst()
                .orElse(null);
        final String clubCode = lines.stream()
                .filter(line -> line.startsWith(INFO_LINE_MARKER))
                .filter(line -> line.contains("Club Code"))
                .map(line -> line.split(":")[1])
                .findFirst()
                .orElse(null);
        return Match.mapBaseMatchInfoFromUSPSAMatchReportFile(matchName, matchDate, clubName, clubCode);
    }

    private static List<Stage> getStages(List<String> lines) {
        //RAW attributes: "G 1,Pistol,18,90,No,08-03,Stage 1 - Dance Boatman Dance,Comstock,1"
        return lines.stream()
                .filter(line -> line.startsWith(STAGE_LINE_MARKER))
                .map(line -> line.split(","))
                .map(Stage::mapStageFromUSPSAMatchReportFile)
                .collect(Collectors.toList());
    }

    private static List<Participant> getParticipants(List<String> lines) {
        //raw line: "E 1, A1234, first, last, No, No, No, No, M, Carry Optics, 738.5825, 1, Minor,etcetc"
        return lines.stream()
                .filter(line -> line.startsWith(PARTICIPANT_LINE_MARKER))
                .map(line -> line.toLowerCase(Locale.ROOT))
                .map(line -> line.split(","))
                .map(Participant::mapParticipantFromUSPSAMatchReportFile)
                .collect(Collectors.toList());
    }

    private static List<ParticipantStageResult> getStageResults(List<String> lines) {
        //raw line: "I Pistol,1,1,No,No,13,0,5,0,0,0,0,2,0,0,0,0,0,0,0,14.56,0,0,0,0,14.56,80,80,5.4945,90.0000,1,"
        return lines.stream()
                .filter(line -> line.startsWith(STAGE_RESULT_LINE_MARKER))
                .map(line -> line.split(","))
                .map(ParticipantStageResult::mapStageResultFromUSPSAMatchReportFile)
                .collect(Collectors.toList());
    }

    private static void writeMatchReport(String outputFile, Match match) {
        try {
            Util.getObjectMapper().writeValue(new File(outputFile), match);
        } catch (IOException e) {
            System.out.println("error reading file");
            System.out.println(e);
        }
    }
}
