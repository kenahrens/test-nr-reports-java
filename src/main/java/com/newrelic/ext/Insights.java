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
	
	public static int parseSimpleQuery(JSONObject response, String function) {
		JSONArray results = response.getJSONArray("results");
		JSONObject jCount = results.getJSONObject(0);
		int iValue = jCount.getInt(function);
		return iValue;
	}
}
