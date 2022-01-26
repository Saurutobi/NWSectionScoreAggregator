package com.saurutobi.NWSectionScoreAggregator;

import com.saurutobi.NWSectionScoreAggregator.Model.Match;
import io.vavr.control.Option;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("ThrowablePrintedToSystemOut")
public class ResultsGenerator {

    public static void generateResultsFromMatch(String inputFileName) {
        Option.of(readMatchFromFile(inputFileName)).peek(match -> {

            final String sundialAward = "name here" + "how long here";




            //final Match match = readMatchFromFile(inputFile);




//                                                   final List<Tuple2<String, List<ParticipantMatchAttendance>>> matchesByAttendance = readMatches(inputDir);
//                                                   final List<String> matchNames = matchesByAttendance.stream()
//                                                           .map(match -> match._1)
//                                                           .collect(Collectors.toList());
//                                                   final List<Entry<Participant, List<ParticipantMatchAttendance>>> allParticipants = createParticipantList(matchesByAttendance);
//
//                                                   //now we have a master list of people and the matches they attended.
//                                                   //first, sort all by Participant's last name.
//                                                   allParticipants.sort(Comparator.comparing(p -> p.getKey().getNameLast()));
//                                                   fillInMissingMatches(allParticipants, matchNames);
//
//                                                   //now we have a list of participants with every match. Print that shit
//
//                                                   //single csv output (or spreadsheet?)
//                                                   //final rows are shooters, columns are matches and match finish, ordered by match date jan-dec left-right (OR, dec-jan left-right)
//                                                   writeSectionReport(outputFile, allParticipants, dqOnly);
                                               });
    }

    private static Match readMatchFromFile(String inputFileName) {
        try {
            return Util.getObjectMapper().readValue(new File(inputFileName), Match.class);
        } catch (IOException e) {
            System.out.println("error reading file");
            System.out.println(e);
            return null;
        }
    }

//    private static List<Tuple2<String, List<ParticipantMatchAttendance>>> readMatches(String inputDirectory) {
//        final File[] files = new File(inputDirectory).listFiles();
//        final List<Tuple2<String, List<ParticipantMatchAttendance>>> matches = new ArrayList<>();
//        Option.of(files).peek(fs -> {
//            for (File file : fs) {
//                final String matchName = file.getName();
//                matches.add(new Tuple2<>(matchName, readFile(file, matchName)));
//            }
//        });
//
//        return matches;
//    }
//
//    private static List<ParticipantMatchAttendance> readFile(File file, String matchName) {
//        final ArrayList<ParticipantMatchAttendance> participants = new ArrayList<>();
//        try {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
//            String line;
//            reader.readLine(); // read header and ignore
//            while ((line = reader.readLine()) != null) {
//                participants.add(mapParticipantFromMatchImportFile(line.split(DELIMITER), matchName));
//            }
//        } catch (IOException e) {
//            System.out.println("error reading file");
//            System.out.println(e);
//        }
//        return participants;
//    }
//
//    private static List<Entry<Participant, List<ParticipantMatchAttendance>>> createParticipantList(List<Tuple2<String, List<ParticipantMatchAttendance>>> matchesByAttendance) {
//        final HashMap<Participant, List<ParticipantMatchAttendance>> allParticipants = new HashMap<>();
//        for (Tuple2<String, List<ParticipantMatchAttendance>> currentMatch : matchesByAttendance) {
//            for (ParticipantMatchAttendance participant : currentMatch._2) {
//                if (allParticipants.containsKey(participant.getParticipant())) { //TODO: maybe do this via USPSA number
//                    allParticipants.get(participant.getParticipant()).add(participant);
//                } else {
//                    final ArrayList<ParticipantMatchAttendance> participantsMatches = new ArrayList<>();
//                    participantsMatches.add(participant);
//                    allParticipants.put(participant.getParticipant(), participantsMatches);
//                }
//            }
//        }
//        return new ArrayList<>(allParticipants.entrySet());
//    }
//
//    private static void fillInMissingMatches(List<Entry<Participant, List<ParticipantMatchAttendance>>> allParticipants, List<String> matchNames) {
//        //For each Participant, go through the MatchNames. If it's not in the Attendances, add it with that boolean flag off.
//        for (Entry<Participant, List<ParticipantMatchAttendance>> personMatches : allParticipants) {
//            final List<String> personsMatchNames = personMatches.getValue().stream()
//                    .map(ParticipantMatchAttendance::getMatchName)
//                    .collect(Collectors.toList());
//            for (String matchName : matchNames) {
//                if (!personsMatchNames.contains(matchName)) {
//                    final ParticipantMatchAttendance skippedMatch = ParticipantMatchAttendance.builder()
//                            .participant(personMatches.getKey())
//                            .isDQed(false)
//                            .matchName(matchName)
//                            .attended(false)
//                            .build();
//                    personMatches.getValue().add(skippedMatch);
//                }
//            }
//            //when all are added for Participant, sort by matchname
//            personMatches.getValue().sort(Comparator.comparing(ParticipantMatchAttendance::getMatchName));
//        }
//    }
//
//    private static void writeSectionReport(String outputFileName, List<Entry<Participant, List<ParticipantMatchAttendance>>> allRecords, boolean dqOnly) {
//        try {
//            final FileWriter myWriter = new FileWriter(outputFileName);
//            writeSectionReportHeader(myWriter, allRecords.get(0).getValue(), dqOnly);
//            for (Entry<Participant, List<ParticipantMatchAttendance>> record : allRecords) {
//                final StringBuilder out = new StringBuilder(record.getKey().getNamefirst() + "|" + record.getKey().getNameLast() + "|" + record.getKey().getUspsaNumber());
//                for (ParticipantMatchAttendance match : record.getValue()) {
//                    if (match.attended) {
//                        if(dqOnly){
//                            out.append("|")
//                                    .append(match.isDQed? "DQ":"__");
//                        }else{
//                        out.append("|")
//                                .append("Division:")
//                                .append(match.division)
//                                .append(" DivisionFinish:")
//                                .append(match.divisonFinish)
//                                .append(" isQDed:")
//                                .append(match.isDQed);
//                        }
//                    } else {
//                        out.append("|");
//                    }
//                }
//                out.append("\n");
//                String printValue = out.toString();
//                if (dqOnly && !printValue.contains("DQ")) {
//                    printValue = "";
//                }
//                System.out.print(printValue);
//                myWriter.write(printValue);
//            }
//            myWriter.flush();
//            myWriter.close();
//        } catch (IOException e) {
//            System.out.println("error writing file");
//            System.out.println(e);
//        }
//    }
//
//    private static void writeSectionReportHeader(FileWriter myWriter, List<ParticipantMatchAttendance> matches, boolean dqOnly) throws IOException {
//        final StringBuilder out = new StringBuilder("First|Last|USPSA Number");
//        for (ParticipantMatchAttendance match : matches) {
//            out.append("|").append(match.getMatchName());
//        }
//        myWriter.write(out + "\n");
//    }
}
