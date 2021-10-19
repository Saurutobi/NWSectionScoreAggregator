package com.saurutobi.NWSectionScoreAggregator.Model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MatchAttendance {
    public String matchName;
    public boolean attendedMatch;
}
