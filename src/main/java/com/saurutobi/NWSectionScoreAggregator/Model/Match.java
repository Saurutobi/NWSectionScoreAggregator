package com.saurutobi.NWSectionScoreAggregator.Model;

import static com.saurutobi.NWSectionScoreAggregator.Util.removeLinePrefix;

import java.time.format.DateTimeFormatter;
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
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public static Match mapBaseMatchInfoFromUSPSAMatchReportFile(String[] attributes) {
        //RAW attributes: "A Name,ClubName,Date"
        return Match.builder()
                .name(removeLinePrefix(attributes[0]))
                .date(LocalDate.parse(attributes[2], formatter))
                .build();
    }
}
