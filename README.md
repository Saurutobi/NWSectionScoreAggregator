# NWSectionScoreAggregator

##How To Run

###Convert a single match from the USPSA Classifier Report
```-i [inputfile]```

This outputs to ```[inputfile].json```

###Bulk Convert USPSA Classifier Reports
```-ia  [input directory] [output directory]```

Example

```-ia "C:\Git\NWSectionScoreAggregator\src\main\resources\2021_RawMatches" "C:\Git\NWSectionScoreAggregator\src\main\resources\2021_ConvertedMatches"```

###Generate custom awards output from a converted match
```-r [input file]```

This outputs to the console

Example

```-r "C:\Git\NWSectionScoreAggregator\src\main\resources\2021_ConvertedMatches\11-14 Paul Bunyan November.txt.json"```

###Aggregate all converted matches into a report
``` -a [input directory] [output file]```

Example

```-a "C:\Git\NWSectionScoreAggregator\src\main\resources\2021_ConvertedMatches" "C:\Git\NWSectionScoreAggregator\src\main\resources\NWSectionMatchReport.csv"```

###Aggregate all converted matches into a DQ report
```-d [input folder] [output file path]```

Example

```-d "C:\Git\NWSectionScoreAggregator\src\main\resources\2021_ConvertedMatches" "C:\Git\NWSectionScoreAggregator\src\main\resources\NWSectionDQReport.csv"```


## TODO
- Print only matches that have DQs for DQ report
- Use json schema to generate targets which are the Model rather than hardcoding a model, give that schema to PS and USPSA