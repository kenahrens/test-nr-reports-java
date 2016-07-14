package com.newrelic.ext;

/**
 * Hello world!
 *
 */
public class App 
{
	public static final String ACCOUNT_ID = "newrelic.accountId";
	public static final String INSIGHTS_QUERY_KEY = "newrelic.insightsQueryKey";
	
	public static final String INSIGHTS_HEADER = "X-Query-Key";
	public static final String INSIGHTS_URL = "https://insights-api.newrelic.com/v1/accounts/{accountId}/query";

	
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    }
}
