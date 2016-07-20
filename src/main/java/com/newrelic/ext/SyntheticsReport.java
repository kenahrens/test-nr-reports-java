package com.newrelic.ext;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.HashMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.json.JSONArray;
import org.json.JSONObject;

import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.typesafe.config.Config;

public class SyntheticsReport {
	
	public static final String monitorList = "SELECT uniques(monitorName) FROM SyntheticCheck";
	public static final String querySuccess = "SELECT percentage(count(*), WHERE result = 'SUCCESS') FROM SyntheticCheck"; 
	public static final String queryFailed = "SELECT percentage(count(*), WHERE result != 'SUCCESS') FROM SyntheticCheck"; 
	public static final String queryDuration = "SELECT average(duration) FROM SyntheticCheck TIMESERIES"; 
	
	private Config conf;
	private Document document;
	private PdfWriter writer;
	private PdfPTable resultsTable;
	
	private HashMap<String,JSONObject> insightsData;
	
	public SyntheticsReport(Config conf) {
		this.conf = conf;
		insightsData = new HashMap<String, JSONObject>();
	}
	
	public void makeReport() throws FileNotFoundException, DocumentException {
		
		// Open the PDF
		String nickname = conf.getString(ReportBuilder.NICKNAME);
		document = new Document(PageSize.LETTER, 50, 50, 50, 50);
		FileOutputStream out = new FileOutputStream(nickname + ".pdf");
		writer = PdfWriter.getInstance(document, out);
		document.open();
		
		// Report Title
		addTitle(nickname + " Report");
		
		// Get a list of all Synthetic monitors
		JSONObject rspMonitorList = Insights.runQuery(conf, addTimeToNrql(monitorList));
		String[] monitors = Insights.parseUniquesResponse(rspMonitorList);
		for(int i=0; i<monitors.length; i++) {
			getMonitorData(monitors[i]);
		}
		
		// Start the table
		startTable();
		
		// Add the monitor data to the table
		for(int i=0; i<monitors.length; i++) {
			addMonitorToTable(monitors[i]);
		}
		
		// Add the completed table
		document.add(resultsTable);
		document.newPage();
		
		// Add the graphs
		for(int i=0; i<monitors.length; i++) {
			addTitle(monitors[i]);
			addMonitorGraph(monitors[i]);
			document.newPage();
		}
		
		// Close the PDF
		document.close();
		System.out.println("Document closed");
	}

	private void getMonitorData(String monitorName) {
		// Make the Insights queries
		JSONObject rspSuccess = Insights.runQuery(conf, addToNrql(querySuccess, monitorName));
		JSONObject rspFailed = Insights.runQuery(conf, addToNrql(queryFailed, monitorName));
		JSONObject rspDuration = Insights.runQuery(conf, addToNrql(queryDuration, monitorName));
		
		insightsData.put(monitorName + ".rspSuccess", rspSuccess);
		insightsData.put(monitorName + ".rspFailed", rspFailed);
		insightsData.put(monitorName + ".rspDuration", rspDuration);
	}
	
	private void addTitle(String title) throws DocumentException {
		Paragraph p = new Paragraph(title);
		p.setAlignment(Paragraph.ALIGN_CENTER);
		document.add(p);
		document.add(Chunk.NEWLINE);
	}
	
	private void startTable() {
		// Create the results table
		// Reference: http://developers.itextpdf.com/examples/itext-action-second-edition/chapter-4
		resultsTable = new PdfPTable(4);
		resultsTable.setWidthPercentage(100);
		PdfPCell cell;
		cell = new PdfPCell(new Phrase("Results"));
		cell.setColspan(4);
		cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		resultsTable.addCell(cell);
		resultsTable.addCell("Monitor Name");
		resultsTable.addCell("Success Rate (%)");
		resultsTable.addCell("Failure Rate (%)");
		resultsTable.addCell("Duration (ms)");
	}
	
	
	public void addMonitorToTable(String monitorName) throws FileNotFoundException, DocumentException {

		JSONObject rspSuccess = insightsData.get(monitorName + ".rspSuccess");
		JSONObject rspFailed = insightsData.get(monitorName + ".rspFailed");
		JSONObject rspDuration = insightsData.get(monitorName + ".rspDuration");
		
		// Parse the Insights results
		double pctSuccess = Insights.parseSimpleResponse(rspSuccess, "result");
		double pctFailed = Insights.parseSimpleResponse(rspFailed, "result");
		double duration = Insights.parseTimeseriesResponse(rspDuration, "average");
		
		resultsTable.addCell(monitorName);
		resultsTable.addCell(getDecimal(pctSuccess));
		resultsTable.addCell(getDecimal(pctFailed));
		resultsTable.addCell(getDecimal(duration));
	}
	
	public void addMonitorGraph(String monitorName) {
		JSONObject rspDuration = insightsData.get(monitorName + ".rspDuration");
		
		// Create the time series line chart
		// Reference http://developers.itextpdf.com/examples/itext-action-second-edition/chapter-14
		PdfContentByte cb = writer.getDirectContent();
        float width = PageSize.LETTER.getWidth();
        float height = PageSize.LETTER.getHeight() / 2;
        PdfTemplate line = cb.createTemplate(width, height);
        Graphics2D g2d2 = new PdfGraphics2D(line, width, height);
        Rectangle2D r2d2 = new Rectangle2D.Double(0, 0, width, height);
        getLineChart(monitorName, rspDuration).draw(g2d2, r2d2);
        g2d2.dispose();
        cb.addTemplate(line, 0, 0);
	}
	
	private JFreeChart getLineChart(String title, JSONObject responseDuration) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
		// Loop through the responseDuration JSON
		JSONArray tsArr = responseDuration.getJSONArray("timeSeries");
		for (int i=0; i < tsArr.length(); i++) {
			JSONObject tsObject = tsArr.getJSONObject(i);
			JSONArray resultsArr = tsObject.getJSONArray("results");
			double avg = resultsArr.getJSONObject(0).getDouble("average");
			
			dataset.addValue(avg, "default", i + "");
		}
		
		String categoryAxisLabel = "";
		String valueAxisLabel = "";
		
		return ChartFactory.createLineChart(title, categoryAxisLabel, valueAxisLabel, dataset,
				PlotOrientation.VERTICAL, false, false, false);
	}
	
	private String getDecimal(double value) {
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(value);
	}
	
	private String addTimeToNrql(String nrql) {
		String fullQuery = new String(nrql);
		fullQuery += " SINCE 1 week ago";
		return fullQuery;
	}
	
	private String addToNrql(String nrql, String monitorName) {
		String fullQuery = new String(nrql);
		fullQuery += " WHERE monitorName = '" + monitorName + "'";
		fullQuery += " SINCE 1 week ago";
		return fullQuery;
	}
}
