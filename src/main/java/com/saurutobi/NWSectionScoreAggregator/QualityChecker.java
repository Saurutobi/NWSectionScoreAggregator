package com.saurutobi.NWSectionScoreAggregator;

import static com.saurutobi.NWSectionScoreAggregator.Util.readConvertedMatches;

import com.saurutobi.NWSectionScoreAggregator.Model.Match;
import com.saurutobi.NWSectionScoreAggregator.Model.Participant;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.control.Option;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class QualityChecker {
    private static final String NO_USPSA_NUMBER = "NO_USPSA_NUMBER";

    public static void qcMatches(String inputDirectory) {
        Option.of(inputDirectory).peek(inputDir -> {
            final List<Match> matches = readConvertedMatches(inputDir);
            qcUspsaNumbers(matches);
            qcShooterNames(matches);
        });
    }

    private static void qcUspsaNumbers(List<Match> matches) {
        for (Match match : matches) {
            boolean foundIssue = false;
            final StringBuilder out = new StringBuilder("Found issue with: " + match.name);
            for (Participant participant : match.getParticipants()) {
                if (participant.getUspsaNumber().equals(NO_USPSA_NUMBER)) {
                    foundIssue = true;
                    out.append("Invalid USPSA Number for: ")
                            .append(participant.getNameFirst())
                            .append(" ")
                            .append(participant.getNameLast())
                            .append(" Number:")
                            .append(participant.getUspsaNumber())
                            .append("/n");
                }
            }
            if (foundIssue) {
                System.out.println(out);
            }
        }
    }

    private static void qcShooterNames(List<Match> matches) {
        System.out.println("Review Aggregated Matches to find duplicates");
        final List<Participant> allParticipants = getAllDistinctMembersWhoParticipated(matches);

        final Set<Tuple2<String, String>> dupeChecker = new HashSet<>();
        for (Participant particpant : allParticipants) {
            if (!dupeChecker.add(new Tuple2<>(particpant.getNameFirst(), particpant.getNameLast()))) {
                System.out.println("Duplicate Participant: " + particpant.getNameFirst() + "," + particpant.getNameLast() + "," + particpant.getUspsaNumber());
            }
        }
    }

    private static List<Participant> getAllDistinctMembersWhoParticipated(List<Match> matches) {
        return io.vavr.collection.List.ofAll(matches.stream()
                                                     .map(Match::getParticipants)
                                                     .flatMap(List::stream)
                                                     .collect(Collectors.toList()))
                .distinctBy(participant -> participant.getNameFirst() + participant.getNameLast() + participant.getUspsaNumber())
                .sorted(Comparator.comparing(Participant::getNameLast))
                .toJavaList();
    }
}
