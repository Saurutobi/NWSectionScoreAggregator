package com.saurutobi.NWSectionScoreAggregator.Model;

import static com.saurutobi.NWSectionScoreAggregator.Util.removeLinePrefix;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Participant {
    private static final String DQ_PISTOL_YES = "yes";
    private static final String DQ_PISTOL_NO = "no";
    private static final String NO_USPSA_NUMBER = "NO_USPSA_NUMBER";
    private static final String USPSA_NUMBER_PREFIX_LIFE = "l";
    private static final String USPSA_NUMBER_PREFIX_ANNUAL = "a";
    private static final String USPSA_NUMBER_PREFIX_B = "b";
    private static final String USPSA_NUMBER_PREFIX_THREEYEAR = "ty";
    private static final String USPSA_NUMBER_PREFIX_FOREIGN_OR_FIVEYEAR = "f";
    public int shooterNumber;
    public String nameFirst;
    public String nameLast;
    public String uspsaNumber;
    public boolean isDQed;
    public Division division;
    public Integer divisonFinish;

    public static Participant mapParticipantFromUSPSAMatchReportFile(String[] attributes) {
        //RAW attributes: "E 1, A1234, first, last, No, No, No, No, M, Carry Optics, 738.5825, 1, Minor,etcetc"
        return Participant.builder()
                .shooterNumber(Integer.parseInt(removeLinePrefix(attributes[0])))
                .nameFirst(attributes[2])
                .nameLast(attributes[3])
                .uspsaNumber(handleUspsaNumber(attributes))
                .isDQed(mapDQPistolValueToBoolean(attributes[4]))
                .division(Division.mapDivisionString(attributes[9]))
                .divisonFinish(Integer.parseInt(attributes[11]))
                .build();
    }

    private static String handleUspsaNumber(String[] attributes) {
        if(isNotValidUspsaNumber(attributes[1])){
            System.out.println("INVALID USPSA NUMBER: " + attributes[1] + "," + attributes[2] + "," + attributes[3]);
            return NO_USPSA_NUMBER;
        }
        return stripNonNumbers(attributes[1]);
    }


    private static boolean isNotValidUspsaNumber(String uspsaNumber) {
        if (uspsaNumber.isEmpty()) {
            return true;
        } else if (uspsaNumber.contains(" ") || uspsaNumber.contains("-") || uspsaNumber.contains("/")) {
            return true;
        } else {
            return !uspsaNumber.contains(USPSA_NUMBER_PREFIX_LIFE)
                   && !uspsaNumber.contains(USPSA_NUMBER_PREFIX_ANNUAL)
                   && !uspsaNumber.contains(USPSA_NUMBER_PREFIX_B)
                   && !uspsaNumber.contains(USPSA_NUMBER_PREFIX_THREEYEAR)
                   && !uspsaNumber.contains(USPSA_NUMBER_PREFIX_FOREIGN_OR_FIVEYEAR);
        }
    }

    private static String stripNonNumbers(String uspsaNumber) {
        return uspsaNumber.replaceAll("[^0-9]", "");
    }

    private static boolean mapDQPistolValueToBoolean(String dqPistolValue) {
        return Match(dqPistolValue).of(
                Case($(DQ_PISTOL_YES), true),
                Case($(DQ_PISTOL_NO), false),
                Case($(), false));
    }
}
