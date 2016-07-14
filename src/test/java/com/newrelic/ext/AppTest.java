package com.newrelic.ext;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
	
	private Config conf;
	
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
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
        return new TestSuite( AppTest.class );
    }

    public void testPass()
    {
        assertTrue( true );
    }
    
    public void testConfigValue()
    {
    	
    	String accountId = conf.getString(App.ACCOUNT_ID);
    	System.out.println("From config file: " + App.ACCOUNT_ID + "=" + accountId);
    	
    	assertNotNull(accountId);
    }
    
    public void testAPI() throws IOException
    {
    	// First get the configuration items 
    	String accountId = conf.getString(App.ACCOUNT_ID);
    	String insightsQueryKey = conf.getString(App.INSIGHTS_QUERY_KEY);
    	
    	// NRQL query
    	String nrql = "SELECT count(*) FROM Transaction";
    	
    	try {
    		HttpResponse<JsonNode> jsonResponse = Unirest.get(App.INSIGHTS_URL)
    			.routeParam("accountId", accountId)
    			.header(App.INSIGHTS_HEADER, insightsQueryKey)
				.queryString("nrql", nrql)
				.asJson();
    		
    		// Parse the JSON response into a String
    		StringWriter writer = new StringWriter();
    		IOUtils.copy(jsonResponse.getRawBody(), writer, "UTF-8");
    		String theString = writer.toString();
    		System.out.println(theString);
    		
    		// Read the JSON response as an object
    		JSONObject response = jsonResponse.getBody().getObject();
    		JSONArray results = response.getJSONArray("results");
    		JSONObject jCount = results.getJSONObject(0);
    		int iCount = jCount.getInt("count");

    		System.out.println("results=" + results);
    		System.out.println("count=" + iCount);
    		
    		// Need to get a HTTP OK 200 response
    		System.out.println("HTTP status from Insights: " + jsonResponse.getStatus());
    		assertEquals(jsonResponse.getStatus(), 200);
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
