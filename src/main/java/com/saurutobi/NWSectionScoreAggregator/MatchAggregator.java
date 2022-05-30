package com.saurutobi.NWSectionScoreAggregator;

import static com.saurutobi.NWSectionScoreAggregator.Util.readConvertedMatches;

import com.saurutobi.NWSectionScoreAggregator.Model.Match;
import com.saurutobi.NWSectionScoreAggregator.Model.Participant;
import io.vavr.Tuple3;
import io.vavr.control.Option;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings("ThrowablePrintedToSystemOut")
public class MatchAggregator {
    private static final String DELIMITER = "|";

    public static void aggregateMatch(String inputDirectory, String outputFileName, boolean dqOnly) {
        Option.of(inputDirectory).peek(inputDir -> Option.of(outputFileName).peek(outputFile -> {
            final List<Match> matches = readConvertedMatches(inputDir);
            if (dqOnly) {
                writeSectionDqReport(outputFile, matches, getAllDistinctMembersWhoParticipatedAndDqed(matches));
            } else {
                writeSectionMatchReport(outputFile, matches, getAllDistinctMembersWhoParticipated(matches));
            }
        }));
    }

    private static List<Tuple3<String, String, String>> getAllDistinctMembersWhoParticipatedAndDqed(List<Match> matches) {
        return io.vavr.collection.List.ofAll(matches.stream()
                                                     .map(Match::getParticipants)
                                                     .flatMap(List::stream)
                                                     .collect(Collectors.toList())
                                                     .stream()
                                                     .filter(participant -> participant.isDQed)
                                                     .map(participant -> new Tuple3<>(
                                                             participant.getNameFirst(),
                                                             participant.getNameLast(),
                                                             participant.getUspsaNumber()))
                                                     .collect(Collectors.toList()))
                .distinctBy(participant -> participant._1 + participant._2 + participant._3)
                .sorted(Comparator.comparing(a -> a._2))
                .toJavaList();
    }

    private static List<Tuple3<String, String, String>> getAllDistinctMembersWhoParticipated(List<Match> matches) {
        return io.vavr.collection.List.ofAll(matches.stream()
                                                     .map(Match::getParticipants)
                                                     .flatMap(List::stream)
                                                     .collect(Collectors.toList())
                                                     .stream()
                                                     .map(participant -> new Tuple3<>(
                                                             participant.getNameFirst(),
                                                             participant.getNameLast(),
                                                             participant.getUspsaNumber()))
                                                     .collect(Collectors.toList()))
                .distinctBy(participant -> participant._1 + participant._2 + participant._3)
                .sorted(Comparator.comparing(a -> a._2))
                .toJavaList();
    }

    private static void writeSectionDqReport(String outputFileName, List<Match> matches, List<Tuple3<String, String, String>> participants) {
        try {
            final FileWriter myWriter = new FileWriter(outputFileName);
            final List<Match> matchesWithDqs = matches.stream()
                    .filter(MatchAggregator::matchContainsDq)
                    .collect(Collectors.toList());

            writeSectionReportHeader(myWriter, matchesWithDqs);
            for (Tuple3<String, String, String> participant : participants) {
                final StringBuilder out = new StringBuilder(participant._1 + DELIMITER + participant._2);
                for (Match match : matchesWithDqs) {
                    final Optional<Participant> participantAtMatch = match.participants.stream()
                            .filter(matchParticipant -> matchParticipant.getNameFirst().equals(participant._1) &&
                                                        matchParticipant.getNameLast().equals(participant._2) &&
                                                        matchParticipant.getUspsaNumber().equals(participant._3))
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
                final StringBuilder out = new StringBuilder(participant._1 + DELIMITER + participant._2);
                for (Match match : matches) {
                    final Optional<Participant> participantAtMatch = match.participants.stream()
                            .filter(matchParticipant -> matchParticipant.getNameLast().equals(participant._2) && matchParticipant.getUspsaNumber().equals(participant._3))
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
        final StringBuilder out = new StringBuilder("First|Last");
        for (Match match : matches) {
            out.append(DELIMITER)
                    .append(match.name.replace(",", ""));
        }
        myWriter.write(out + "\n");
    }
}
