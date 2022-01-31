package com.saurutobi.NWSectionScoreAggregator.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Match {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    public String name;
    public LocalDate date;
    public String clubName;
    public String clubCode;
    public List<Participant> participants;
    public List<Stage> stages;
    public List<ParticipantStageResult> participantStageResults;

    public static Match mapBaseMatchInfoFromUSPSAMatchReportFile(String name, String date, String clubName, String clubCode) {
        return Match.builder()
                .name(name)
                .date(LocalDate.parse(date, formatter))
                .clubName(clubName)
                .clubCode(clubCode)
                .build();
    }
}
