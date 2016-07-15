package com.newrelic.ext;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.json.JSONArray;
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
        
        // Load the configuration file
        conf = ConfigFactory.load();
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
    
    public void testAPI()
    {
    	// NRQL query
    	String nrql = "SELECT count(*) FROM Transaction";
    	
    	JSONObject response = Insights.runQuery(conf, nrql);
    	int iCount = Insights.parseSimpleQuery(response, "count");
		System.out.println("count=" + iCount);
    }
}
