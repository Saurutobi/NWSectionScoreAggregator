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

    private static final String CARRY_OPTICS_STRING = "Carry Optics";
    private static final String LIMITED_STRING = "Limited";
    private static final String LIMITED_10_STRING = "Limited 10";
    private static final String OPEN_STRING = "Open";
    private static final String PCC_STRING = "PCC";
    private static final String PRODUCTION_STRING = "Production";
    private static final String REVOLVER_STRING = "Revolver";
    private static final String SINGLE_STACK_STRING = "Single Stack";

    public static Division mapDivisionString(String division) {
        return Match(division).of(
                Case($(CARRY_OPTICS_STRING), CARRY_OPTICS),
                Case($(LIMITED_STRING), LIMITED),
                Case($(LIMITED_10_STRING), LIMITED_10),
                Case($(OPEN_STRING), OPEN),
                Case($(PCC_STRING), PCC),
                Case($(PRODUCTION_STRING), PRODUCTION),
                Case($(REVOLVER_STRING), REVOLVER),
                Case($(SINGLE_STACK_STRING), SINGLE_STACK));
    }
}
