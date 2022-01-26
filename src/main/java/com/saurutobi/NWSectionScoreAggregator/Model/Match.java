package com.saurutobi.NWSectionScoreAggregator.Model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class Match {
    public String name;
    public LocalDate date;
    public String clubName;
    public String clubCode;
    public List<Participant> participants;
    public List<Stage> stages;
    public List<ParticipantStageResult> participantStageResults;

    public static Match mapBaseMatchInfoFromUSPSAMatchReportFile(String[] attributes) {
        //RAW attributes: "Name,ClubName,Date"
        return Match.builder()
                .name(attributes[0])
                .date(LocalDate.parse(attributes[2]))
                .build();
    }
}
