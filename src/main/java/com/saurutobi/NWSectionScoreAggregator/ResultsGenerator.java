package com.saurutobi.NWSectionScoreAggregator;

import static java.util.stream.Collectors.groupingBy;

import com.saurutobi.NWSectionScoreAggregator.Model.Match;
import com.saurutobi.NWSectionScoreAggregator.Model.Participant;
import com.saurutobi.NWSectionScoreAggregator.Model.ParticipantStageResult;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.control.Option;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class ResultsGenerator {
    public static void generateResultsFromMatch(String inputFileName) {
        Option.of(readMatchFromFile(inputFileName)).peek(match -> {
            final String sundial = "Sundial Award: " + getSundialAward(match);
            final String noShootAssassin = "NoShoot Assassin Award: " + getNoShootAssassin(match);
            final String dawson = "Dawson Award for most Misses: " + getDawson(match);
            final String iDoWhatIWant = "I Do What I Want Award for most Procedurals: " + getIDoWhatIWant(match);
            final String iLikeTheD = "I Like The D Award for most Ds: " + getILikeTheD(match);
            final String aimHarder = "Aim Harder Award for most As: " + getAimHarder(match);

            System.out.println(sundial);
            System.out.println(noShootAssassin);
            System.out.println(dawson);
            System.out.println(iDoWhatIWant);
            System.out.println(iLikeTheD);
            System.out.println(aimHarder);
        });
    }

    @SuppressWarnings("ThrowablePrintedToSystemOut")
    private static Match readMatchFromFile(String inputFileName) {
        try {
            return Util.getObjectMapper().readValue(new File(inputFileName), Match.class);
        } catch (IOException e) {
            System.out.println("error reading file");
            System.out.println(e);
            return null;
        }
    }

    private static String getSundialAward(Match match) {
        List<Tuple2<Integer, Double>> personTime = match.getParticipantStageResults().stream()
                .collect(groupingBy(ParticipantStageResult::getShooterNumber))
                .entrySet().stream()
                .map(scores -> {
                    final Double totalTime = scores.getValue().stream()
                            .map(ParticipantStageResult::getTime)
                            .mapToDouble(Double::valueOf)
                            .sum();
                    return Tuple.of(scores.getKey(), totalTime);
                })
                .sorted((a, b) -> b._2.compareTo(a._2))
                .collect(Collectors.toList());
        final Participant participant = match.participants.stream().filter(part -> part.getShooterNumber() == personTime.get(0)._1).findFirst().get();
        return participant.getNameFirst() + " " + participant.getNameLast() + ", Total Time Taken: " + personTime.get(0)._2;
    }

    private static String getNoShootAssassin(Match match) {
        return doResult(match, ParticipantStageResult::getCountNoShoot, ", Total NoShoots Assassinated: ");
    }

    private static String getDawson(Match match) {
        return doResult(match, ParticipantStageResult::getCountMiss, ", Total Misses: ");
    }

    private static String getIDoWhatIWant(Match match) {
        return doResult(match, ParticipantStageResult::getCountProcedural, ", Total Procedurals: ");
    }

    private static String getILikeTheD(Match match) {
        return doResult(match, ParticipantStageResult::getCountD, ", Total Ds: ");
    }

    private static String getAimHarder(Match match) {
        return doResult(match, ParticipantStageResult::getCountA, ", Total As: ");
    }

    private static String doResult(Match match, Function<ParticipantStageResult, Integer> itemFunction, String resultPrint) {
        List<Tuple3<Integer, Integer, Double>> personMissesTime = match.getParticipantStageResults().stream()
                .collect(groupingBy(ParticipantStageResult::getShooterNumber))
                .entrySet().stream()
                .map(scores -> {
                    final Integer totalMisses = scores.getValue().stream()
                            .map(itemFunction)
                            .mapToInt(Integer::valueOf)
                            .sum();
                    final Double totalTime = scores.getValue().stream()
                            .map(ParticipantStageResult::getTime)
                            .mapToDouble(Double::valueOf)
                            .sum();
                    return Tuple.of(scores.getKey(), totalMisses, totalTime);
                })
                .sorted((a, b) -> b._2.compareTo(a._2))
                .collect(Collectors.toList());

        //handle ties
        if (Objects.equals(personMissesTime.get(0)._2, personMissesTime.get(1)._2)) {
            List<Tuple3<Integer, Integer, Double>> tiebreaker = personMissesTime.stream()
                    .filter(item -> item._2.equals(personMissesTime.get(0)._2))
                    .sorted(Comparator.comparing(a -> a._3))
                    .collect(Collectors.toList());
            final Participant participant = match.participants.stream().filter(part -> part.getShooterNumber() == tiebreaker.get(0)._1).findFirst().get();
            return participant.getNameFirst() + " " + participant.getNameLast() + resultPrint + tiebreaker.get(0)._2;
        } else {
            final Participant participant = match.participants.stream().filter(part -> part.getShooterNumber() == personMissesTime.get(0)._1).findFirst().get();
            return participant.getNameFirst() + " " + participant.getNameLast() + resultPrint + personMissesTime.get(0)._2;
        }
    }
}
