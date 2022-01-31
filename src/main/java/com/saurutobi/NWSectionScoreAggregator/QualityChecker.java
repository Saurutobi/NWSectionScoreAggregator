package com.saurutobi.NWSectionScoreAggregator;

import static com.saurutobi.NWSectionScoreAggregator.Util.readConvertedMatches;

import com.saurutobi.NWSectionScoreAggregator.Model.Match;
import com.saurutobi.NWSectionScoreAggregator.Model.Participant;
import io.vavr.control.Option;

import java.util.List;

public class QualityChecker {
    private static final String NO_USPSA_NUMBER = "NO_USPSA_NUMBER";

    public static void qcMatches(String inputDirectory) {
        Option.of(inputDirectory).peek(inputDir -> {
            final List<Match> matches = readConvertedMatches(inputDir);
            for (Match match : matches) {
                System.out.println("Checking " + match.name);
                for (Participant participant : match.getParticipants()) {
                    if (isNotValidUspsaNumber(participant.getUspsaNumber())) {
                        String toprint = "Invalid USPSA Number for: " + participant.getNameFirst() + " " + participant.getNameLast() + " Number:" + participant.getUspsaNumber();
                        System.out.println(toprint);
                    }
                }
            }
        });
    }

    private static boolean isNotValidUspsaNumber(String uspsaNumber) {
        if (uspsaNumber.equals(NO_USPSA_NUMBER)) {
            return true;
        } else if (uspsaNumber.contains(" ")) {
            return true;
        } else {
            return !uspsaNumber.contains("l") && !uspsaNumber.contains("a") && !uspsaNumber.contains("b") && !uspsaNumber.contains("ty") && !uspsaNumber.contains("fy");
        }
    }
}
