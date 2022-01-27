package com.saurutobi.NWSectionScoreAggregator.Model;

import static com.saurutobi.NWSectionScoreAggregator.Util.removeLinePrefix;

import java.time.format.DateTimeFormatter;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Match {
    public String name;
    public LocalDate date;
    public String clubName;
    public String clubCode;
    public List<Participant> participants;
    public List<Stage> stages;
    public List<ParticipantStageResult> participantStageResults;
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public static Match mapBaseMatchInfoFromUSPSAMatchReportFile(String name, String date, String clubName, String clubCode) {
        return Match.builder()
                .name(name)
                .date(LocalDate.parse(date, formatter))
                .clubName(clubName)
                .clubCode(clubCode)
                .build();
    }
}
