# NWSectionScoreAggregator

##How To Run

###Convert a single match from the USPSA Classifier Report
```-i [inputfile]```

This outputs to ```[inputfile].json```

###Bulk Convert USPSA Classifier Reports
```-ia  [input directory] [output directory]```

Example

```-ia "C:\Git\NWSectionScoreAggregator\src\main\resources\2022_RawMatches" "C:\Git\NWSectionScoreAggregator\src\main\resources\2022_ConvertedMatches"```

###Quality Check converted matches
```-qc [input folder]```

This outputs matches, participants where USPSA numbers aren't valid to the console

Example

```-qc "C:\Git\NWSectionScoreAggregator\src\main\resources\2022_ConvertedMatches"```


###Generate custom awards output from a converted match
```-r [input file]```

This awards outputs to the console

Example

```-r "C:\Git\NWSectionScoreAggregator\src\main\resources\2022_ConvertedMatches\01-09 Paul Bunyan January 2022.txt.json"```

###Aggregate all converted matches into a report
``` -a [input directory] [output file]```

Example

```-a "C:\Git\NWSectionScoreAggregator\src\main\resources\2022_ConvertedMatches" "C:\Git\NWSectionScoreAggregator\src\main\resources\NWSectionMatchReport.csv"```

###Aggregate all converted matches into a DQ report
```-d [input folder] [output file path]```

Example

```-d "C:\Git\NWSectionScoreAggregator\src\main\resources\2022_ConvertedMatches" "C:\Git\NWSectionScoreAggregator\src\main\resources\NWSectionDQReport.csv"```


## TODO
- strip USPSA numbers down to just the numbers-update the QC
- Use json schema to generate targets which are the Model rather than hardcoding a model, give that schema to PS and USPSA
- Add Section Series calculations