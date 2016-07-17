# Test NR Reports Java
[![Build Status](https://travis-ci.org/kenahrens/test-nr-reports-java.svg?branch=master)](https://travis-ci.org/kenahrens/test-nr-reports-java)
Utility to generate PDF reports

## Pre-Requisites
* Maven (dependencies should be in pom.xml)
* Oracle Java 1.7

## Installation Instructions
* Clone the repository
* Either set environment variables (see below) or duplicate default.conf to make your own configuration
* Run the tests ```mvn test```

## Env Variables Configuration
Set 4 environment variables to the correct values for your account (verify with [API Keys](https://rpm.newrelic.com/apikeys)))
* NEWRELIC_ACCOUNT_ID - you will also see this in the URL bar
* NEWRELIC_REST_API_KEY - overall REST Key (legacy)
* NEWRELIC_ADMIN_API_KEY - specific Admin user API Key, used for certain API calls
* NEWRELIC_INSIGHTS_QUERY_KEY - there are keys just for Insights in the Manage Data section

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
