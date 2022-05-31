package com.saurutobi.NWSectionScoreAggregator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saurutobi.NWSectionScoreAggregator.Model.Match;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Util {
    public static String removeLinePrefix(String attribute) {
        return attribute.substring(2);
    }

    public static ObjectMapper getObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        return objectMapper;
    }

    public static List<Match> readConvertedMatches(String inputDirectory) {
        final List<Match> matches = new ArrayList<>();
        FileUtils.listFiles(new File(inputDirectory), null, true).forEach(file -> matches.add(readFile(file)));
        return matches;
    }

    private static Match readFile(File file) {
        try {
            return Util.getObjectMapper().readValue(file, Match.class);
        } catch (IOException e) {
            System.out.println("error reading file | " + e);
            return null;
        }
    }
}
