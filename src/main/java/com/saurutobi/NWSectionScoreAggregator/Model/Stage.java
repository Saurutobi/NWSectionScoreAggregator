package com.saurutobi.NWSectionScoreAggregator.Model;

import static com.saurutobi.NWSectionScoreAggregator.Util.removeLinePrefix;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class Stage {
    public int number;
    public String name;
    public int roundCount;
    public int points;
    public ScoringType scoringType;
    public int stringCount;

    public static Stage mapStageFromUSPSAMatchReportFile(String[] attributes) {
        //RAW attributes: "G 1,Pistol,18,90,No,08-03,Stage 1 - Dance Boatman Dance,Comstock,1"
        return Stage.builder()
                .number(Integer.parseInt(removeLinePrefix(attributes[0])))
                .name(attributes[6])
                .roundCount(Integer.parseInt(attributes[2]))
                .points(Integer.parseInt(attributes[3]))
                .scoringType(ScoringType.mapScoringTypeString(attributes[7]))
                .stringCount(Integer.parseInt(attributes[8]))
                .build();
    }
}
