package com.newrelic.ext;

import java.io.File;
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
	public static final String NICKNAME = "newrelic.nickname";

	public static final String INSIGHTS_HEADER = "X-Query-Key";
	public static final String INSIGHTS_URL = "https://insights-api.newrelic.com/v1/accounts/{accountId}/query";

	private Config conf;

	public ReportBuilder() {
		// Load the configuration file
		conf = loadConf();
        System.out.println("Configuration loaded for: " + conf.getString(NICKNAME));
	}

	public static Config loadConf() {
		// Load the configuration file if it exists
		String confFile = System.getProperty("CONFIG_FILE");
		File file = new File("config" + File.separatorChar + "default.conf");

		// Make sure it's a proper file
		if (confFile != null) {
			File testFile = new File(confFile);
			if (testFile.exists()) {
				file = testFile;
			}
		}
		System.out.println("Using config file: " + file.getName());
		Config tempConf = ConfigFactory.parseFile(file);
		tempConf = tempConf.resolve();
		return tempConf;
	}

	public void makeSyntheticReport() {
		SyntheticsReport report = new SyntheticsReport(conf);
		try {
			report.makeReport();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Make Report Complete");
	}

    public Config getConfig() {
    	return conf;
    }

    public static void main( String[] args )
    {
        System.out.println( "Starting Report Builder" );
        ReportBuilder builder = new ReportBuilder();
        builder.makeSyntheticReport();
    }

}
