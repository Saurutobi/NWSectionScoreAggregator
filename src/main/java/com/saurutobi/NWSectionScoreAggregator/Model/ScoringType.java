package com.saurutobi.NWSectionScoreAggregator.Model;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

public enum ScoringType {
    COMSTOCK,
    VIRGINIA;

    private static final String COMSTOCK_STRING = "Comstock";
    private static final String VIRGINIA_STRING = "Virginia";

    public static ScoringType mapScoringTypeString(String division) {
        return Match(division).of(
                Case($(COMSTOCK_STRING), COMSTOCK),
                Case($(VIRGINIA_STRING), VIRGINIA));
    }
}
