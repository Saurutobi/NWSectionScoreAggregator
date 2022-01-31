package com.saurutobi.NWSectionScoreAggregator.Model;

import static com.saurutobi.NWSectionScoreAggregator.Util.removeLinePrefix;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

import io.vavr.control.Option;
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
    private static final String NOT_A_MEMBER = "NOT_A_MEMBER";
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
                .uspsaNumber(handleUspsaNumber(attributes[1]))
                .isDQed(mapDQPistolValueToBoolean(attributes[4]))
                .division(Division.mapDivisionString(attributes[9]))
                .divisonFinish(Integer.parseInt(attributes[11]))
                .build();
    }

    private static String handleUspsaNumber(String uspsaNumber) {
        return uspsaNumber.isEmpty() ? NOT_A_MEMBER : uspsaNumber;
    }

    private static boolean mapDQPistolValueToBoolean(String dqPistolValue) {
        return Match(dqPistolValue).of(
                Case($(DQ_PISTOL_YES), true),
                Case($(DQ_PISTOL_NO), false),
                Case($(), false));
    }
}
