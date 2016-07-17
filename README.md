# Test NR Reports Java
Utility to generate PDF reports

## Pre-Requisites
Maven (dependencies should be in pom.xml)
Java 1.7 or newer

## Installation Instructions
* Clone the repository
* Copy config/default.conf to application.conf
* Edit your application.conf and supply the correct API Key values
* Run the tests ```mvn test```

## Test Output
When you run the tests it will build (and download dependencies), then you should see output like this.

```
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running com.newrelic.ext.ReportBuilderTest
From config file: newrelic.accountId=726352
SELECT count(*) FROM Transaction
HTTP status from Insights: 200
count = 313.0
SELECT percentage(count(*), WHERE result = 'SUCCESS') FROM SyntheticCheck
HTTP status from Insights: 200
percentage = 100.0
SELECT average(duration) FROM SyntheticCheck TIMESERIES
HTTP status from Insights: 200
average = 7546.985630507299
Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.686 sec

Results :

Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
```

