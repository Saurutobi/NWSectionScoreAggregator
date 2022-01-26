package com.saurutobi.NWSectionScoreAggregator.Model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParticipantStageResult {
    public int stageNumber;
    public int shooterNumber;
    public double time;
    public int stageFinish;
    public double hitFactor;
    public int countA;
    public int countC;
    public int countD;
    public int countMiss;
    public int countNoShoot;

    public static ParticipantStageResult mapStageResultFromUSPSAMatchReportFile(String[] attributes) {
        //RAW attributes: "I Pistol,1,1,No,No,13,0,5,0,0,0,0,2,0,0,0,0,0,0,0,14.56,0,0,0,0,14.56,80,80,5.4945,90.0000,1,"
        return ParticipantStageResult.builder()
                .stageNumber(Integer.parseInt(attributes[1]))
                .shooterNumber(Integer.parseInt(attributes[2]))
                .time(Double.parseDouble(attributes[25]))
                .stageFinish(Integer.parseInt(attributes[30]))
                .hitFactor(Double.parseDouble(attributes[28]))
                .countA(Integer.parseInt(attributes[5]))
                .countC(Integer.parseInt(attributes[7]))
                .countD(Integer.parseInt(attributes[8]))
                .countMiss(Integer.parseInt(attributes[9]))
                .countNoShoot(Integer.parseInt(attributes[10]))
                .build();
    }
}
