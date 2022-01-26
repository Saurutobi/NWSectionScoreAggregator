package com.saurutobi.NWSectionScoreAggregator;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Util {
    public static String removeLinePrefix(String attribute) {
        return attribute.substring(3);
    }

    public static ObjectMapper getObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        return objectMapper;
    }
}
