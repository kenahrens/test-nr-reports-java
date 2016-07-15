package com.newrelic.ext;

import org.json.JSONObject;

import com.typesafe.config.Config;

public class SyntheticsReport {
	
	public static final String monitorName = "MEAN Login and Test";
	public static final String querySuccess = "SELECT percentage(count(*), WHERE result = 'SUCCESS') FROM SyntheticCheck"; 
	public static final String queryFailed = "SELECT percentage(count(*), WHERE result != 'SUCCESS') FROM SyntheticCheck"; 
	public static final String queryDuration = "SELECT average(duration) FROM SyntheticCheck"; 
	
	private Config conf; 
	
	public SyntheticsReport(Config conf) {
		this.conf = conf;
	}
	
	public void makeReport() {
		
		JSONObject r1 = Insights.runQuery(conf, addToNrql(querySuccess));
		JSONObject r2 = Insights.runQuery(conf, addToNrql(queryFailed));
		JSONObject r3 = Insights.runQuery(conf, addToNrql(queryDuration));
		
		int p1 = Insights.parseSimpleQuery(r1, "result");
		
	}
	
	private String addToNrql(String nrql) {
		String fullQuery = new String(nrql);
		fullQuery += " WHERE monitorName = '" + monitorName + "'";
		fullQuery += " SINCE 1 month ago";
		return fullQuery;
	}
}
