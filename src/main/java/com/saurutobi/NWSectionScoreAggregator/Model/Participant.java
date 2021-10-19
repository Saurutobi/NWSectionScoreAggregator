package com.saurutobi.NWSectionScoreAggregator.Model;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Participant {
    private static final String DQ_PISTOL_YES = "Yes";
    private static final String DQ_PISTOL_NO = "No";
    public String namefirst;
    public String nameLast;
    public String uspsaNumber; //TODO:should this be JUST the number part? I think so
    public boolean isDQed;
    public Division division;
    public Integer divisonFinish;

    public static Participant mapParticipantFromFile(String[] attributes) {
        //RAW attributes: "1, A122842, Jonathan, Tran, No, No, No, No, M, Carry Optics, 738.5825, 1, Minor,etcetc"
        return Participant.builder()
                .namefirst(attributes[2])
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
