package com.newrelic.ext;

public class SyntheticsReport {
	
	public static final String monitorName = "MEAN Login and Test";
	public static final String q1 = "SELECT percentage(count(*), WHERE result = 'SUCCESS') FROM SyntheticCheck"; 
	public static final String q2 = "SELECT percentage(count(*), WHERE result != 'SUCCESS') FROM SyntheticCheck"; 
	public static final String q3 = "SELECT average(duration) FROM SyntheticCheck"; 
	
	
}
