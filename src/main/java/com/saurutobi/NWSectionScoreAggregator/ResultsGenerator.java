package com.saurutobi.NWSectionScoreAggregator;

import com.saurutobi.NWSectionScoreAggregator.Model.Match;
import io.vavr.control.Option;

import java.io.File;
import java.io.IOException;

public class ResultsGenerator {

    public static void generateResultsFromMatch(String inputFileName) {
        Option.of(readMatchFromFile(inputFileName)).peek(match -> {
            final String sundial = "Sundial Award: " + getSundialAward(match);
            final String noShootAssassin = "NoShoot Assassin Award: " + getNoShootAssassin(match);
            final String dawson = "Dawson Award for most Misses: " + getDawson(match);
            final String iDoWhatIWant = "I Do What I Want Award for most Procedurals: " + getIDoWhatIWant(match);
            final String iLikeTheD = "I Like The D Award for most Ds: " + getILikeTheD(match);
            final String aimHarder = "Aim Harder Award for most As: " + getAimHarder(match);
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

    private static String getSundialAward(Match match){
        //stream the ParticipantStageResults, reduce to shooternumber, add the time together.
        //sort by time, get highest, then get that shooternumber
        //get the Participant with that shooternumber
        //return that shit

        return "name goes here" + ", Total Time Taken: " + "raw time goes here";
    }

    private static String getNoShootAssassin(Match match){
        //stream the ParticipantStageResults, reduce to shooternumber, add the noshoots together.
        //sort by noshoots, get highest, then get that shooternumber
        //get the Participant with that shooternumber
        //return that shit

        return "name goes here" + ", Total NoShoots Assassinated: " + "NoShoot count goes here";
    }

    private static String getDawson(Match match){
        //stream the ParticipantStageResults, reduce to shooternumber, add the misses together.
        //sort by misses, get highest, then get that shooternumber
        //get the Participant with that shooternumber
        //return that shit

        return "name goes here" + ", Total Misses: " + "Miss count goes here";
    }

    private static String getIDoWhatIWant(Match match){
        //stream the ParticipantStageResults, reduce to shooternumber, add the procedurals together.
        //sort by procedurals, get highest, then get that shooternumber
        //get the Participant with that shooternumber
        //return that shit

        return "name goes here" + ", Total Procedurals: " + "Procedural count goes here";
    }

    private static String getILikeTheD(Match match){
        //stream the ParticipantStageResults, reduce to shooternumber, add the Ds together.
        //sort by Ds, get highest, then get that shooternumber
        //get the Participant with that shooternumber
        //return that shit

        return "name goes here" + ", Total Ds: " + "D count goes here";
    }

    private static String getAimHarder(Match match){
        //stream the ParticipantStageResults, reduce to shooternumber, add the As together.
        //sort by As, get highest, then get that shooternumber
        //get the Participant with that shooternumber
        //return that shit

        return "name goes here" + ", Total As: " + "A count goes here";
    }
}
