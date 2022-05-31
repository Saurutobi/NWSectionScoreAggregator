package com.saurutobi.NWSectionScoreAggregator.Model.SectionSeries;

import com.saurutobi.NWSectionScoreAggregator.Model.Division;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeriesParticipant {
    public String nameFirst;
    public String nameLast;
    public String uspsaNumber;
    public Division division;
    public Classification classification;
    public Double seriesMatchPoints;
    public int seriesMatchAs;
    public int seriesMatchDs;
    public int seriesMatchNoShoots;
}
