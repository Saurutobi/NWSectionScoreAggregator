package com.saurutobi.NWSectionScoreAggregator.Model;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

public enum Division {
    CARRY_OPTICS,
    LIMITED,
    LIMITED_10,
    OPEN,
    PCC,
    PRODUCTION,
    REVOLVER,
    SINGLE_STACK;

    private static final String CARRY_OPTICS_STRING = "carry optics";
    private static final String CO_STRING = "co";
    private static final String LIMITED_STRING = "limited";
    private static final String LIMITED_10_STRING = "limited 10";
    private static final String OPEN_STRING = "open";
    private static final String PCC_STRING = "pcc";
    private static final String PRODUCTION_STRING = "production";
    private static final String REVOLVER_STRING = "revolver";
    private static final String SINGLE_STACK_STRING = "single stack";

    public static Division mapDivisionString(String division) {
        return Match(division).of(
                Case($(CARRY_OPTICS_STRING), CARRY_OPTICS),
                Case($(CO_STRING), CARRY_OPTICS),
                Case($(LIMITED_STRING), LIMITED),
                Case($(LIMITED_10_STRING), LIMITED_10),
                Case($(OPEN_STRING), OPEN),
                Case($(PCC_STRING), PCC),
                Case($(PRODUCTION_STRING), PRODUCTION),
                Case($(REVOLVER_STRING), REVOLVER),
                Case($(SINGLE_STACK_STRING), SINGLE_STACK));
    }
}
