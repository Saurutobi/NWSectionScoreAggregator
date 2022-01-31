package com.saurutobi.NWSectionScoreAggregator;

import com.saurutobi.NWSectionScoreAggregator.Model.Match;
import com.saurutobi.NWSectionScoreAggregator.Model.Participant;
import io.vavr.Tuple3;
import io.vavr.control.Option;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings("ThrowablePrintedToSystemOut")
public class SectionAggregator {
    private static final String DELIMITER = "|";

    public static void aggregateMatch(String inputDirectory, String outputFileName, boolean dqOnly) {
        Option.of(inputDirectory).peek(inputDir -> Option.of(outputFileName).peek(outputFile -> {
            final List<Match> matches = readConvertedMatches(inputDir);
            if (dqOnly) {
                writeSectionDqReport(outputFile, matches, getAllDistinctMembersWhoParticipatedByUspsaNumber(matches));
            } else {
                writeSectionMatchReport(outputFile, matches, getAllDistinctMembersWhoParticipatedByUspsaNumber(matches));
            }
        }));
    }

    private static List<Tuple3<String, String, String>> getAllDistinctMembersWhoParticipatedByUspsaNumber(List<Match> matches) {
        return io.vavr.collection.List.ofAll(matches.stream()
                                                     .map(Match::getParticipants)
                                                     .flatMap(List::stream)
                                                     .collect(Collectors.toList())
                                                     .stream()
                                                     .map(participant -> new Tuple3<>(
                                                             participant.getNameFirst(),
                                                             participant.getNameLast(),
                                                             participant.getUspsaNumber()))
                                                     .distinct()
                                                     .collect(Collectors.toList()))
                .distinctBy(participant -> participant._3)
                .sorted(Comparator.comparing(a -> a._2))
                .toJavaList();
    }

    private static List<Match> readConvertedMatches(String inputDirectory) {
        final List<Match> matches = new ArrayList<>();
        FileUtils.listFiles(new File(inputDirectory), null, true).forEach(file -> matches.add(readFile(file)));
        return matches;
    }

    private static Match readFile(File file) {
        try {
            return Util.getObjectMapper().readValue(file, Match.class);
        } catch (IOException e) {
            System.out.println("error reading file");
            System.out.println(e);
            return null;
        }
    }

    private static void writeSectionDqReport(String outputFileName, List<Match> matches, List<Tuple3<String, String, String>> participants) {
        try {
            final FileWriter myWriter = new FileWriter(outputFileName);
            final List<Match> matchesWithNoDqs = matches.stream()
                    .filter(SectionAggregator::matchContainsDq)
                    .collect(Collectors.toList());

            writeSectionReportHeader(myWriter, matchesWithNoDqs);
            for (Tuple3<String, String, String> participant : participants) {
                final StringBuilder out = new StringBuilder(participant._1 + DELIMITER + participant._2 + DELIMITER + participant._3);
                for (Match match : matchesWithNoDqs) {
                    final Optional<Participant> participantAtMatch = match.participants.stream()
                            .filter(matchParticipant -> matchParticipant.getUspsaNumber().equals(participant._3))
                            .findFirst();
                    if (participantAtMatch.isPresent()) {
                        final Participant participantForRecord = participantAtMatch.get();
                        out.append("|")
                                .append(participantForRecord.isDQed ? "DQ" : "__");
                    } else {
                        out.append(DELIMITER)
                                .append("__");
                    }
                }
                out.append("\n");
                String printValue = out.toString();

                //Remove those that never DQed
                if (!printValue.contains("DQ")) {
                    printValue = "";
                }
                System.out.print(printValue);
                myWriter.write(printValue);
            }
            myWriter.flush();
            myWriter.close();
        } catch (IOException e) {
            System.out.println("error writing file");
            System.out.println(e);
        }
    }

    private static boolean matchContainsDq(Match match) {
        return match.participants.stream()
                .anyMatch(participant -> participant.isDQed);
    }

    private static void writeSectionMatchReport(String outputFileName, List<Match> matches, List<Tuple3<String, String, String>> participants) {
        try {
            final FileWriter myWriter = new FileWriter(outputFileName);
            writeSectionReportHeader(myWriter, matches);
            for (Tuple3<String, String, String> participant : participants) {
                final StringBuilder out = new StringBuilder(participant._1 + DELIMITER + participant._2 + DELIMITER + participant._3);
                for (Match match : matches) {
                    final Optional<Participant> participantAtMatch = match.participants.stream()
                            .filter(matchParticipant -> matchParticipant.getUspsaNumber().equals(participant._3))
                            .findFirst();
                    if (participantAtMatch.isPresent()) {
                        final Participant participantForRecord = participantAtMatch.get();

                        out.append("|")
                                .append("Division:")
                                .append(participantForRecord.getDivision())
                                .append(" DivisionFinish:")
                                .append(participantForRecord.getDivisonFinish())
                                .append(" isQDed:")
                                .append(participantForRecord.isDQed);
                    } else {
                        out.append(DELIMITER)
                                .append("__");
                    }
                }
                out.append("\n");
                String printValue = out.toString();
                System.out.print(printValue);
                myWriter.write(printValue);
            }
            myWriter.flush();
            myWriter.close();
        } catch (IOException e) {
            System.out.println("error writing file");
            System.out.println(e);
        }
    }

    private static void writeSectionReportHeader(FileWriter myWriter, List<Match> matches) throws IOException {
        final StringBuilder out = new StringBuilder("First|Last|USPSA Number");
        for (Match match : matches) {
            out.append(DELIMITER)
                    .append(match.name.replace(",", ""));
        }
        myWriter.write(out + "\n");
    }
}
