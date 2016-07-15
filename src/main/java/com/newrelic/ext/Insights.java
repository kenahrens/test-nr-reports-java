package com.newrelic.ext;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.typesafe.config.Config;

public class Insights {
	
	public static JSONObject runQuery(Config conf, String nrql) {
    	
		// Get the configuration items 
    	String accountId = conf.getString(ReportBuilder.ACCOUNT_ID);
    	String insightsQueryKey = conf.getString(ReportBuilder.INSIGHTS_QUERY_KEY);
    	System.out.println("Running query: " + nrql);
    	
    	try {
    		HttpResponse<JsonNode> jsonResponse = Unirest.get(ReportBuilder.INSIGHTS_URL)
    			.routeParam("accountId", accountId)
    			.header(ReportBuilder.INSIGHTS_HEADER, insightsQueryKey)
				.queryString("nrql", nrql)
				.asJson();
    		
    		System.out.println("HTTP status from Insights: " + jsonResponse.getStatus());

    		// Read the JSON response as an object
    		JSONObject response = jsonResponse.getBody().getObject();
    		return response;
    		
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	// Return null on query failure
    	return null;
	}
	
	public static double parseSimpleResponse(JSONObject response, String function) {
		JSONArray resultsArr = response.getJSONArray("results");
		return resultsArr.getJSONObject(0).getDouble(function);
	}
	
	public static void parseFacetResponse() {
		// TODO
	}
	
	/**
	 * Timeseries response will have a few objects:
	 * 
	 * "total": { "results": [ { "FUNCTION": VALUE } ] }
	 * "timeseries": [ { "results": [ { "FUNCTION": VALUE } ], "beginTimeSeconds": VALUE, "endTimeSeconds": VALUE, ...
	 * "metadata": BLAH
	 */
	public static double parseTimeseriesResponse(JSONObject response, String function) {
		JSONObject totalObj = response.getJSONObject("total");
		JSONArray resultsArr = totalObj.getJSONArray("results");
		return resultsArr.getJSONObject(0).getDouble(function);
	}
}
