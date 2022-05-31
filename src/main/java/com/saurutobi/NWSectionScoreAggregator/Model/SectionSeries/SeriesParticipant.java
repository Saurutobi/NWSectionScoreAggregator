package com.saurutobi.NWSectionScoreAggregator.Model.SectionSeries;

import static com.saurutobi.NWSectionScoreAggregator.Util.removeLinePrefix;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

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
    public Double seriesMatchPoints;
    public int seriesMatchAs;
    public int seriesMatchDs;
    public int seriesMatchNoShoots;
}
