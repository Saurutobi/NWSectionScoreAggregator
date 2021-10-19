package com.saurutobi.NWSectionScoreAggregator;

import com.saurutobi.NWSectionScoreAggregator.Model.Participant;
import io.vavr.control.Option;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class NWSectionScoreAggregatorApplication {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("No args, exiting");
            System.exit(0);
        } else if (args[0].equals("-i")) {
            System.out.println("importing match");
            importMatch(args[1]);
        } else if (args[0].equals("-a")) {
            System.out.println("aggregating matches");

            //aggregate individual match into main collection
            //read directory
            //combine all
            //single csv output (or spreadsheet?)
            //final rows are shooters, columns are matches and match finish, ordered by match date jan-dec left-right (OR, dec-jan left-right)

        }

//
//        final FileWriter myWriter = new FileWriter("src/main/resources/some file.txt");
//
//                String out = "|" + thing + "||" + thing2 + "||";
//                System.out.println(out);
//                myWriter.write(out + "\n");
//
//        myWriter.flush();
//        myWriter.close();
    }

    private static void importMatch(String fileName) {
        Option.of(fileName).peek(file -> {
            final List<String> lines = readFile(file);
            final List<Participant> participants = filterParticipants(lines);
            //spit out csv
        });
    }

    private static List<String> readFile(String fileName) {
        final ArrayList<String> lines = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            //ignored
        }
        return lines;
    }

    private static List<Participant> filterParticipants(List<String> lines) {
        //grab all lines starting with E, split by space then split part 2 by commas
        //raw line: "E 1, A122842, Jonathan, Tran, No, No, No, No, M, Carry Optics, 738.5825, 1, Minor,etcetc"
//
//        final List<String> rawParticipants = lines.stream()
//                .map(line -> line.split(" ")[1])
//                .collect(Collectors.toList());
//
//        return rawParticipants.stream()
//                .map(line -> line.split(","))
//                .map(Participant::mapParticipantFromFile)
//                .collect(Collectors.toList());

        return lines.stream()
                .map(line -> line.split(" ")[1])
                .map(line -> line.split(","))
                .map(Participant::mapParticipantFromFile)
                .collect(Collectors.toList());
    }
}
