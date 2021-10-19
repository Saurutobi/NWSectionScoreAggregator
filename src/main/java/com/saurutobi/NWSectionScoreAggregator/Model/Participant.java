package com.saurutobi.NWSectionScoreAggregator.Model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Participant {
    public String namefirst;
    public String nameLast;
    public String uspsaNumber; //TODO:should this be JUST the number part? I think so
}
