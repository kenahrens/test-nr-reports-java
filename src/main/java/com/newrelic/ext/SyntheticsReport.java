package com.newrelic.ext;

import org.json.JSONObject;

import com.typesafe.config.Config;

public class SyntheticsReport {
	
	public static final String monitorName = "MEAN Login and Test";
	public static final String q1 = "SELECT percentage(count(*), WHERE result = 'SUCCESS') FROM SyntheticCheck"; 
	public static final String q2 = "SELECT percentage(count(*), WHERE result != 'SUCCESS') FROM SyntheticCheck"; 
	public static final String q3 = "SELECT average(duration) FROM SyntheticCheck"; 
	
	private Config conf; 
	
	public SyntheticsReport(Config conf) {
		this.conf = conf;
	}
	
	public void makeReport() {
		JSONObject r1 = Insights.runQuery(conf, q1);
		JSONObject r2 = Insights.runQuery(conf, q2);
		JSONObject r3 = Insights.runQuery(conf, q3);
		
		
	}
}
