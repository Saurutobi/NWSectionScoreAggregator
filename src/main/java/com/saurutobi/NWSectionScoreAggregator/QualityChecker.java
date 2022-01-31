package com.saurutobi.NWSectionScoreAggregator;

import com.saurutobi.NWSectionScoreAggregator.Model.Match;
import com.saurutobi.NWSectionScoreAggregator.Model.Participant;
import io.vavr.Tuple3;
import io.vavr.control.Option;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings("ThrowablePrintedToSystemOut")
public class QualityChecker {
    public static void qcMatches(String inputDirectory) {
        Option.of(inputDirectory).peek(inputDir ->  {
            final List<Match> matches = readConvertedMatches(inputDir);

            //report on matches


        });
    }

    private static List<Match> readConvertedMatches(String inputDirectory) {
        final List<Match> matches = new ArrayList<>();
        FileUtils.listFiles(new File(inputDirectory), null, true).forEach(file -> matches.add(readFile(file)));
        return matches;
    }

    private static Match readFile(File file) {
        try {
            return Util.getObjectMapper().readValue(file, Match.class);
        } catch (IOException e) {
            System.out.println("error reading file");
            System.out.println(e);
            return null;
        }
    }
}
