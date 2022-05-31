package com.saurutobi.NWSectionScoreAggregator;

import static com.saurutobi.NWSectionScoreAggregator.Util.readConvertedMatches;

import com.saurutobi.NWSectionScoreAggregator.Model.Division;
import com.saurutobi.NWSectionScoreAggregator.Model.Match;
import com.saurutobi.NWSectionScoreAggregator.Model.Participant;
import com.saurutobi.NWSectionScoreAggregator.Model.ParticipantStageResult;
import com.saurutobi.NWSectionScoreAggregator.Model.SectionSeries.SeriesMatch;
import com.saurutobi.NWSectionScoreAggregator.Model.SectionSeries.SeriesParticipant;
import io.vavr.Function1;
import io.vavr.Tuple2;
import io.vavr.collection.Map;
import io.vavr.control.Option;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SectionSeriesAggregator {
    private static final int numberOfRequiredSectionMatches = 4;

    public static void createSeriesResults(String inputDirectory, String outputFileName) {
        Option.of(inputDirectory).peek(inputDir -> Option.of(outputFileName).peek(outputFile -> {
            final List<Match> matches = readConvertedMatches(inputDir);
            final List<SeriesMatch> seriesMatchesWithAllSectionMemberParticipation = loadParticipantsIntoSeriesMatches(matches);
            final List<SeriesMatch> seriesMatchesWithOnlyEligibleParticipation = filterOutParticipantsWithNotEnoughRequiredMatches(seriesMatchesWithAllSectionMemberParticipation);

            //produce Leaderboards
            final List<Tuple2<SeriesParticipant, Double>> sortedHighOverallScores = getHighScores(seriesMatchesWithOnlyEligibleParticipation);
            final List<Tuple2<SeriesParticipant, Integer>> sortedHighAs = getHighValue(seriesMatchesWithOnlyEligibleParticipation, SeriesParticipant::getSeriesMatchAs);
            final List<Tuple2<SeriesParticipant, Integer>> sortedHighDs = getHighValue(seriesMatchesWithOnlyEligibleParticipation, SeriesParticipant::getSeriesMatchDs);
            final List<Tuple2<SeriesParticipant, Integer>> sortedHighNoShoots = getHighValue(seriesMatchesWithOnlyEligibleParticipation, SeriesParticipant::getSeriesMatchNoShoots);
            /*leaderboards:
                HOA-total points across all matches shot
                Most Accurate - most As (currently on all 7 matches)
                What No-Shoot - most NS (currently on all 7 matches)
                Deltas For Days - most Ds (currently on all 7 matches)
            */


            /*division awards:
                for each division, filter just by participants in that division.
                group by first/last into Map<Tuple2<namefirst, namelast>, List<matchpoints>>
                sort the matchpoints, then sum up the top 4
                Now we have namefirst, namelast, total section points. put that in a list, sort by points, there's the awards

            class awards: do the above, but put another loop inside the division for classes and filter by class
*/
            /*Scores:
                Divisions: for each person in a division, get their match points by getting their finish%(need 1st place for that). Then assign them points to 4 digits by that %
                    Do this for all divisions.
                    Top 3 shooters, 5 shooter minimum.
                Classes/Divisions: Top 1-n shooters, minimum 5, n+1 for every 3 past the initial 5 minimum(eg 8ppl = 2 awards). Division winners do

             */

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
                @SuppressWarnings("OptionalGetWithoutIsPresent")
                final Double firstPlacePoints = divParticipants.stream()
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
                                .seriesMatchPoints(divParticipant.getDivisionPoints() / firstPlacePoints * 100)
                                .seriesMatchAs(getAs(divParticipant.shooterNumber, match.getParticipantStageResults()))
                                .seriesMatchDs(getDs(divParticipant.shooterNumber, match.getParticipantStageResults()))
                                .seriesMatchNoShoots(getNoShoots(divParticipant.shooterNumber, match.getParticipantStageResults()))
                                .build();
                        seriesMatch.getParticipants().add(seriesParticipant);
                    }
                }
            }
            seriesMatches.add(seriesMatch);
        }
        return seriesMatches;
    }

    private static boolean isSectionMember(Participant divParticipant) {
        //TODO: Figure out how to do this later
        return true;
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
        return groupParticipants(allParticipants).toJavaStream()
                .map(entry -> new Tuple2<>(entry._2.get(0), entry._2.toJavaStream()
                        .map(SeriesParticipant::getSeriesMatchPoints)
                        .reduce(0.0, Double::sum)))
                .sorted(Comparator.comparingDouble(Tuple2::_2))
                .collect(Collectors.toList());
    }

    private static List<Tuple2<SeriesParticipant, Integer>> getHighValue(List<SeriesMatch> seriesMatches, Function<SeriesParticipant, Integer> getter) {
        List<SeriesParticipant> allParticipants = seriesMatches.stream()
                .map(SeriesMatch::getParticipants)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        return groupParticipants(allParticipants).toJavaStream()
                .map(entry -> new Tuple2<>(entry._2.get(0), entry._2.toJavaStream()
                        .map(getter)
                        .reduce(0, Integer::sum)))
                .sorted(Comparator.comparingDouble(Tuple2::_2))
                .collect(Collectors.toList());
    }
}
