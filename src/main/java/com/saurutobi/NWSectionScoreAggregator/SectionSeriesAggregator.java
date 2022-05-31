package com.saurutobi.NWSectionScoreAggregator;

import static com.saurutobi.NWSectionScoreAggregator.Util.readConvertedMatches;

import com.saurutobi.NWSectionScoreAggregator.Model.Match;
import io.vavr.control.Option;

import java.util.List;

public class SectionSeriesAggregator {
    public static void createSeriesResults(String inputDirectory, String outputFileName) {
        Option.of(inputDirectory).peek(inputDir -> Option.of(outputFileName).peek(outputFile -> {
            final List<Match> matches = readConvertedMatches(inputDir);
            /*DO QC:
                check USPSA numbers
                compare against list of section members, set internal var for non-members(100% needs to be used for 1st place
                verify person/division participated in enough matches (1st place must still be used to set 100%)
            */

            //DO THIS STUFF:

            /*leaderboards:
                HOA-total points across all matches shot
                Most Accurate - most As (currently on all 7 matches)
                What No-Shoot - most NS (currently on all 7 matches)
                Deltas For Days - most Ds (currently on all 7 matches)
            */


            /*Scores:
                Divisions: for each person in a division, get their match points by getting their finish%(need 1st place for that). Then assign them points to 4 digits by that %
                    Do this for all divisions.
                    Top 3 shooters, 5 shooter minimum.
                Classes/Divisions: Top 1-n shooters, minimum 5, n+1 for every 3 past the initial 5 minimum(eg 8ppl = 2 awards). Division winners do

             */





            /*Order
                Go through each match, map match & participants to internal values
                    map match
                    for each division(get dynamically)
                        get 1st place, set as 100%
                        map participant in division. set match points as divisionpoints/100%points * 100
                            if(isSectionMember(input file, participant) add to participants in match
                calucate points or something. not sure

            */




        }));
    }
}
