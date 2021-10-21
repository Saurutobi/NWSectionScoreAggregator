package com.saurutobi.NWSectionScoreAggregator.Model;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParticipantMatchAttendance {
    private static final String DQ_PISTOL_YES = "yes";
    private static final String DQ_PISTOL_NO = "no";
    public Participant participant;
    public String matchName;
    public boolean isDQed;
    public Division division;
    public Integer divisonFinish;
    public boolean attended;

    public static ParticipantMatchAttendance mapParticipantFromUSPSAMatchReportFile(String[] attributes) {
        //RAW attributes: "1, A122842, Jonathan, Tran, No, No, No, No, M, Carry Optics, 738.5825, 1, Minor,etcetc"
        return ParticipantMatchAttendance.builder()
                .participant(Participant.builder()
                                     .namefirst(attributes[2])
                                     .nameLast(attributes[3])
                                     .uspsaNumber(attributes[1])
                                     .build())
                .isDQed(mapDQPistolValueToBoolean(attributes[4]))
                .division(Division.mapDivisionString(attributes[9]))
                .divisonFinish(Integer.parseInt(attributes[11]))
                .build();
    }

    public static ParticipantMatchAttendance mapParticipantFromMatchImportFile(String[] attributes, String matchName) {
        //RAW attributes: "john|newman||PRODUCTION|7|false"
        return ParticipantMatchAttendance.builder()
                .participant(Participant.builder()
                                     .namefirst(attributes[0])
                                     .nameLast(attributes[1])
                                     .uspsaNumber(attributes[2])
                                     .build())
                .isDQed(Boolean.parseBoolean(attributes[5]))
                .division(Division.valueOf(attributes[3]))
                .divisonFinish(Integer.parseInt(attributes[4]))
                .matchName(matchName)
                .attended(true)
                .build();
    }

    private static boolean mapDQPistolValueToBoolean(String dqPistolValue) {
        return Match(dqPistolValue).of(
                Case($(DQ_PISTOL_YES), true),
                Case($(DQ_PISTOL_NO), false),
                Case($(), false));
    }

    public String toMatchReport(String delimiter) {
        return participant.namefirst + delimiter +
               participant.nameLast + delimiter +
               participant.uspsaNumber + delimiter +
               division + delimiter +
               divisonFinish + delimiter +
               isDQed;
    }
}
