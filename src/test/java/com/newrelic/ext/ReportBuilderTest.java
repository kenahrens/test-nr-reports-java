package com.newrelic.ext;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.File;

import org.json.JSONObject;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Unit test for simple App.
 */
public class ReportBuilderTest
    extends TestCase
{

	private Config conf;

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ReportBuilderTest( String testName )
    {
        super( testName );
        conf = ReportBuilder.loadConf();
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( ReportBuilderTest.class );
    }

    public void testPass()
    {
        assertTrue( true );
    }

    public void testConfigValue()
    {

    	String accountId = conf.getString(ReportBuilder.ACCOUNT_ID);
    	System.out.println("From config file: " + ReportBuilder.ACCOUNT_ID + "=" + accountId);

    	assertNotNull(accountId);
    }

    public void testAPICount()
    {
    	// NRQL query
    	String nrql = "SELECT count(*) FROM Transaction";

    	JSONObject response = Insights.runQuery(conf, nrql);
    	double dCount = Insights.parseSimpleResponse(response, "count");
		System.out.println("count = " + dCount);
    }

    public void testAPIPercent()
    {
    	// NRQL query
    	String nrql = "SELECT percentage(count(*), WHERE result = 'SUCCESS') FROM SyntheticCheck";

    	JSONObject response = Insights.runQuery(conf, nrql);
    	double dPercent = Insights.parseSimpleResponse(response, "result");
		System.out.println("percentage = " + dPercent);
    }

    public void testAPITimeseries()
    {
    	// NRQL query
    	String nrql = "SELECT average(duration) FROM SyntheticCheck TIMESERIES";

    	JSONObject response = Insights.runQuery(conf, nrql);
    	double dAverage = Insights.parseTimeseriesResponse(response, "average");
    	System.out.println("average = " + dAverage);
    }
}
