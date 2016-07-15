package com.newrelic.ext;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.json.JSONObject;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.typesafe.config.Config;

public class SyntheticsReport {
	
	public static final String querySuccess = "SELECT percentage(count(*), WHERE result = 'SUCCESS') FROM SyntheticCheck"; 
	public static final String queryFailed = "SELECT percentage(count(*), WHERE result != 'SUCCESS') FROM SyntheticCheck"; 
	public static final String queryDuration = "SELECT average(duration) FROM SyntheticCheck"; 
	
	private Config conf; 
	
	public SyntheticsReport(Config conf) {
		this.conf = conf;
	}
	
	public void makeReport(String monitorName) throws FileNotFoundException, DocumentException {
		
		// Make the Insights queries
		JSONObject responseSuccess = Insights.runQuery(conf, addToNrql(querySuccess, monitorName));
		JSONObject responseFailed = Insights.runQuery(conf, addToNrql(queryFailed, monitorName));
		JSONObject responseDuration = Insights.runQuery(conf, addToNrql(queryDuration, monitorName));
		
		// Parse the Insights results
		double pctSuccess = Insights.parseSimpleResponse(responseSuccess, "result");
		double pctFailed = Insights.parseSimpleResponse(responseFailed, "result");
		
		// Open the PDF 
		Document document = new Document(PageSize.A4, 50, 50, 50, 50);
		FileOutputStream out = new FileOutputStream(monitorName + ".pdf");
		PdfWriter.getInstance(document, out);
		document.open();
		
		// Create the layout
		
		// Title
		Paragraph title = new Paragraph(monitorName + " Report");
		title.setAlignment(Paragraph.ALIGN_CENTER);
		document.add(title);
		
		// Create the results table
		// Reference: http://developers.itextpdf.com/examples/itext-action-second-edition/chapter-4
		PdfPTable table = new PdfPTable(2);
		PdfPCell cell;
		
		// Add table with the success and failure rate
		cell = new PdfPCell(new Phrase("Results"));
		cell.setColspan(2);
		table.addCell(cell);
		table.addCell("Success Rate (%)");
		table.addCell(Double.toString(pctSuccess));
		table.addCell("Failure Rate (%)");
		table.addCell(Double.toString(pctFailed));
		document.add(table);
		
		// Close the PDF
		document.close();
		
	}
	
	private String addToNrql(String nrql, String monitorName) {
		String fullQuery = new String(nrql);
		fullQuery += " WHERE monitorName = '" + monitorName + "'";
		fullQuery += " SINCE 1 month ago";
		return fullQuery;
	}
}
