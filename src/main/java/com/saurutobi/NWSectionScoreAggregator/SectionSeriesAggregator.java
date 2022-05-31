package com.saurutobi.NWSectionScoreAggregator;

import static com.saurutobi.NWSectionScoreAggregator.Util.readConvertedMatches;
import static java.util.Comparator.comparingDouble;

import com.saurutobi.NWSectionScoreAggregator.Model.Division;
import com.saurutobi.NWSectionScoreAggregator.Model.Match;
import com.saurutobi.NWSectionScoreAggregator.Model.Participant;
import com.saurutobi.NWSectionScoreAggregator.Model.ParticipantStageResult;
import com.saurutobi.NWSectionScoreAggregator.Model.SectionSeries.Classification;
import com.saurutobi.NWSectionScoreAggregator.Model.SectionSeries.SeriesMatch;
import com.saurutobi.NWSectionScoreAggregator.Model.SectionSeries.SeriesParticipant;
import io.vavr.Function1;
import io.vavr.Tuple2;
import io.vavr.collection.Map;
import io.vavr.control.Option;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SectionSeriesAggregator {
    private static final int numberOfRequiredSectionMatches = 0;  //TODO: bump to 4 when that many have been shot
    private static final String classificationRecordFileName = "CHANGEME";

    public static void createSeriesResults(String inputDirectory, String outputFileName) {
        Option.of(inputDirectory).peek(inputDir -> Option.of(outputFileName).peek(outputFile -> {
            final List<Match> matches = readConvertedMatches(inputDir);
            final List<SeriesMatch> seriesMatchesWithAllSectionMemberParticipation = loadParticipantsIntoSeriesMatches(matches);
            final List<SeriesMatch> seriesMatchesWithOnlyEligibleParticipation = filterOutParticipantsWithNotEnoughRequiredMatches(seriesMatchesWithAllSectionMemberParticipation);
            //TODO: print out eligible folks so that an update to the classification file can be made

            produceLeaderboards(seriesMatchesWithOnlyEligibleParticipation);


            for (Division div : Division.values()) {
                final List<Tuple2<SeriesParticipant, Double>> allDivParticipants = getAllDivisionParticipants(seriesMatchesWithOnlyEligibleParticipation, div);
                final List<Tuple2<SeriesParticipant, Double>> top3InDivision = topN(allDivParticipants, 3);

                if (allDivParticipants.size() >= 5) {
                    //print top3 for division IF there's at least 5 in it
                }

                for (Classification classification : Classification.values()) {
                    final List<Tuple2<SeriesParticipant, Double>> allClassParticipants = getAllClassParticipants(allDivParticipants, classification);

                    //print Top 1-n shooters, minimum 5, n+1 for every 3 past the initial 5 minimum(eg 8ppl = 2 awards). Division winners do
                }
            }
        }));
    }

    private static List<SeriesMatch> loadParticipantsIntoSeriesMatches(List<Match> matches) {
        final ArrayList<SeriesMatch> seriesMatches = new ArrayList<>();
        for (Match match : matches) {
            final SeriesMatch seriesMatch = SeriesMatch.builder()
                    .name(match.getName())
                    .date(match.getDate())
                    .participants(new ArrayList<>())
                    .build();
            for (Division div : Division.values()) {
                final List<Participant> divParticipants = match.getParticipants().stream()
                        .filter(participant -> participant.getDivision() == div)
                        .collect(Collectors.toList());
                if (divParticipants.size() > 0) {
                    @SuppressWarnings("OptionalGetWithoutIsPresent") final Double firstPlacePoints = divParticipants.stream()
                            .filter(participant -> participant.getDivisonFinish() == 1)
                            .map(Participant::getDivisionPoints)
                            .findFirst()
                            .get();
                    for (Participant divParticipant : divParticipants) {
                        if (isSectionMember(divParticipant)) {
                            final SeriesParticipant seriesParticipant = SeriesParticipant.builder()
                                    .nameFirst(divParticipant.getNameFirst())
                                    .nameLast(divParticipant.getNameLast())
                                    .uspsaNumber(divParticipant.getUspsaNumber())
                                    .division(div)
                                    .classification(getClassification(divParticipant))
                                    .seriesMatchPoints(divParticipant.getDivisionPoints() / firstPlacePoints * 100)
                                    .seriesMatchAs(getAs(divParticipant.shooterNumber, match.getParticipantStageResults()))
                                    .seriesMatchDs(getDs(divParticipant.shooterNumber, match.getParticipantStageResults()))
                                    .seriesMatchNoShoots(getNoShoots(divParticipant.shooterNumber, match.getParticipantStageResults()))
                                    .build();
                            seriesMatch.getParticipants().add(seriesParticipant);
                        }
                    }
                }
            }
            seriesMatches.add(seriesMatch);
        }
        return seriesMatches;
    }

    private static boolean isSectionMember(Participant divParticipant) {
        //TODO: read from file of section folks and do a ispresent type check
        return true;
    }

    private static Classification getClassification(Participant divParticipant) {
        //TODO: read from classificationRecordFileName of folks eligible(print out folks eligible), then do a get type thing
        return null;
    }

    private static int getAs(int shooterNumber, List<ParticipantStageResult> participantStageResults) {
        return participantStageResults.stream()
                .filter(participantStageResult -> participantStageResult.getShooterNumber() == shooterNumber)
                .map(ParticipantStageResult::getCountA)
                .reduce(0, Integer::sum);
    }

    private static int getDs(int shooterNumber, List<ParticipantStageResult> participantStageResults) {
        return participantStageResults.stream()
                .filter(participantStageResult -> participantStageResult.getShooterNumber() == shooterNumber)
                .map(ParticipantStageResult::getCountD)
                .reduce(0, Integer::sum);
    }

    private static int getNoShoots(int shooterNumber, List<ParticipantStageResult> participantStageResults) {
        return participantStageResults.stream()
                .filter(participantStageResult -> participantStageResult.getShooterNumber() == shooterNumber)
                .map(ParticipantStageResult::getCountNoShoot)
                .reduce(0, Integer::sum);
    }

    private static List<SeriesMatch> filterOutParticipantsWithNotEnoughRequiredMatches(List<SeriesMatch> seriesMatchesWithAllSectionMemberParticipation) {
        List<SeriesParticipant> allParticipants = seriesMatchesWithAllSectionMemberParticipation.stream()
                .map(SeriesMatch::getParticipants)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        List<SeriesParticipant> participantsWithEnoughMatches = groupParticipants(allParticipants).toJavaStream()
                .map(entry -> new Tuple2<>(entry._2.get(0), entry._2.size()))
                .filter(entry -> entry._2 >= numberOfRequiredSectionMatches)
                .map(entry -> entry._1)
                .collect(Collectors.toList());

        final ArrayList<SeriesMatch> seriesMatches = new ArrayList<>();
        for (SeriesMatch match : seriesMatchesWithAllSectionMemberParticipation) {
            final SeriesMatch seriesMatch = SeriesMatch.builder()
                    .name(match.getName())
                    .date(match.getDate())
                    .participants(match.getParticipants().stream()
                                          .filter(participant -> hasEnoughMatches(participant, participantsWithEnoughMatches))
                                          .collect(Collectors.toList()))
                    .build();
            seriesMatches.add(seriesMatch);
        }
        return seriesMatches;
    }

    private static Map<Tuple2<String, String>, io.vavr.collection.List<SeriesParticipant>> groupParticipants(List<SeriesParticipant> participants) {
        final Function1<SeriesParticipant, Tuple2<String, String>> grouper = (participant) -> new Tuple2<>(participant.getNameFirst(), participant.getNameLast());
        return io.vavr.collection.List.ofAll(participants).groupBy(grouper);
    }

    private static boolean hasEnoughMatches(SeriesParticipant seriesParticipant, List<SeriesParticipant> participantsWithEnoughMatches) {
        return participantsWithEnoughMatches.stream()
                .anyMatch(participant -> participant.getNameFirst().equals(seriesParticipant.getNameFirst()) &&
                                         participant.getNameLast().equals(seriesParticipant.getNameLast()));
    }

    private static List<Tuple2<SeriesParticipant, Double>> getHighScores(List<SeriesMatch> seriesMatches) {
        List<SeriesParticipant> allParticipants = seriesMatches.stream()
                .map(SeriesMatch::getParticipants)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        return topN(groupParticipants(allParticipants).toJavaStream()
                            .map(entry -> new Tuple2<>(entry._2.get(0), entry._2.toJavaStream()
                                    .map(SeriesParticipant::getSeriesMatchPoints)
                                    .reduce(0.0, Double::sum)))
                            .sorted(comparingDouble(Tuple2::_2))
                            .collect(Collectors.toList()), 10);
    }

    private static List<Tuple2<SeriesParticipant, Integer>> getHighValue(List<SeriesMatch> seriesMatches, Function<SeriesParticipant, Integer> getter) {
        List<SeriesParticipant> allParticipants = seriesMatches.stream()
                .map(SeriesMatch::getParticipants)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        return topN(groupParticipants(allParticipants).toJavaStream()
                            .map(entry -> new Tuple2<>(entry._2.get(0), entry._2.toJavaStream()
                                    .map(getter)
                                    .reduce(0, Integer::sum)))
                            .sorted(comparingDouble(Tuple2::_2))
                            .collect(Collectors.toList()), 10);
    }

    private static <T extends Number> List<Tuple2<SeriesParticipant, T>> topN(List<Tuple2<SeriesParticipant, T>> participants, int n) {
        final ArrayList<Tuple2<SeriesParticipant, T>> sortedTopN = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            sortedTopN.add(participants.get(participants.size() - i));
        }
        return sortedTopN;
    }

    private static Double getSeriesTotalPoints(List<SeriesParticipant> seriesParticipant) {
        List<Double> scores = seriesParticipant.stream()
                .sorted(comparingDouble(SeriesParticipant::getSeriesMatchPoints))
                .map(SeriesParticipant::getSeriesMatchPoints)
                .collect(Collectors.toList());
        Double score = 0.0;
        for (int i = 1; i <= 4; i++) {
            score += scores.get(scores.size() - i);
        }
        return score;
    }

    private static List<Tuple2<SeriesParticipant, Double>> getAllDivisionParticipants(List<SeriesMatch> seriesMatches, Division div) {
        return groupParticipants(seriesMatches.stream().map(SeriesMatch::getParticipants)
                                         .flatMap(List::stream)
                                         .filter(participant -> participant.getDivision() == div)
                                         .collect(Collectors.toList()))
                .toJavaStream()
                .map(entry -> new Tuple2<>(entry._2.get(0), getSeriesTotalPoints(entry._2.toJavaList())))
                .collect(Collectors.toList());
    }

    private static List<Tuple2<SeriesParticipant, Double>> getAllClassParticipants(List<Tuple2<SeriesParticipant, Double>> participants, Classification classification) {
        return participants.stream()
                .filter(entry -> entry._1.getClassification() == classification)
                .collect(Collectors.toList());
    }

    private static void produceLeaderboards(List<SeriesMatch> seriesMatchesWithOnlyEligibleParticipation){
        //produce Leaderboards
        final List<Tuple2<SeriesParticipant, Double>> sortedHighOverallScores = getHighScores(seriesMatchesWithOnlyEligibleParticipation);
        final List<Tuple2<SeriesParticipant, Integer>> sortedHighAs = getHighValue(seriesMatchesWithOnlyEligibleParticipation, SeriesParticipant::getSeriesMatchAs);
        final List<Tuple2<SeriesParticipant, Integer>> sortedHighDs = getHighValue(seriesMatchesWithOnlyEligibleParticipation, SeriesParticipant::getSeriesMatchDs);
        final List<Tuple2<SeriesParticipant, Integer>> sortedHighNoShoots = getHighValue(seriesMatchesWithOnlyEligibleParticipation, SeriesParticipant::getSeriesMatchNoShoots);
            /*Print leaderbords:
            High Overall - sortedHighOverallScores
            Most Accurate - sortedHighAs
            Deltas For Days - sortedHighDs
            What No-Shoot - sortedHighNoShoots
            */
    }
}

