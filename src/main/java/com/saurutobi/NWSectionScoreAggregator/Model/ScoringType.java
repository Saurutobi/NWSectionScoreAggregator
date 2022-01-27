package com.saurutobi.NWSectionScoreAggregator.Model;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

public enum ScoringType {
    CHRONO,
    COMSTOCK,
    FIXED,
    VIRGINIA;

    private static final String CHRONO_STRING = "Chrono";
    private static final String COMSTOCK_STRING = "Comstock";
    private static final String Fixed_STRING = "Fixed";
    private static final String VIRGINIA_STRING = "Virginia";

    public static ScoringType mapScoringTypeString(String division) {
        return Match(division).of(
                Case($(CHRONO_STRING), CHRONO),
                Case($(COMSTOCK_STRING), COMSTOCK),
                Case($(Fixed_STRING), FIXED),
                Case($(VIRGINIA_STRING), VIRGINIA));
    }
}
