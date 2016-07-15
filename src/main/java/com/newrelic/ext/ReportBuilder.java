package com.newrelic.ext;

import java.io.FileNotFoundException;

import com.itextpdf.text.DocumentException;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Hello world!
 *
 */
public class ReportBuilder 
{
	public static final String ACCOUNT_ID = "newrelic.accountId";
	public static final String INSIGHTS_QUERY_KEY = "newrelic.insightsQueryKey";
	
	public static final String INSIGHTS_HEADER = "X-Query-Key";
	public static final String INSIGHTS_URL = "https://insights-api.newrelic.com/v1/accounts/{accountId}/query";

	private Config conf;
	
	public ReportBuilder() {
		conf = ConfigFactory.load();
	}
	
	public void makeSyntheticReport() {
		SyntheticsReport report = new SyntheticsReport(conf);
        
		try {
			report.makeReport("MEAN Login and Test");
		} catch (FileNotFoundException | DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    public Config getConfig() {
    	return conf;
    }
    

    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        ReportBuilder builder = new ReportBuilder();
        builder.makeSyntheticReport();
    }
    
}
