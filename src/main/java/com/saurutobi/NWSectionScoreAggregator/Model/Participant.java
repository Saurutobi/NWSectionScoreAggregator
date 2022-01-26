package com.saurutobi.NWSectionScoreAggregator.Model;

import static com.saurutobi.NWSectionScoreAggregator.Util.removeLinePrefix;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Participant {
    private static final String DQ_PISTOL_YES = "yes";
    private static final String DQ_PISTOL_NO = "no";
    public int shooterNumber;
    public String nameFirst;
    public String nameLast;
    public String uspsaNumber;
    public String matchName;
    public boolean isDQed;
    public Division division;
    public Integer divisonFinish;
    public boolean attended;

    public static Participant mapParticipantFromUSPSAMatchReportFile(String[] attributes) {
        //RAW attributes: "E 1, A1234, first, last, No, No, No, No, M, Carry Optics, 738.5825, 1, Minor,etcetc"
        return Participant.builder()
                .shooterNumber(Integer.parseInt(removeLinePrefix(attributes[0])))
                .nameFirst(attributes[2])
                .nameLast(attributes[3])
                .uspsaNumber(attributes[1])
                .isDQed(mapDQPistolValueToBoolean(attributes[4]))
                .division(Division.mapDivisionString(attributes[9]))
                .divisonFinish(Integer.parseInt(attributes[11]))
                .build();
    }

    private static boolean mapDQPistolValueToBoolean(String dqPistolValue) {
        return Match(dqPistolValue).of(
                Case($(DQ_PISTOL_YES), true),
                Case($(DQ_PISTOL_NO), false),
                Case($(), false));
    }
}
