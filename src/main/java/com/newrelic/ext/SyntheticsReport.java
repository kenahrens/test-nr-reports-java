package com.newrelic.ext;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DecimalFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;
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
	
	public static final String querySuccess = "SELECT percentage(count(*), WHERE result = 'SUCCESS') FROM SyntheticCheck"; 
	public static final String queryFailed = "SELECT percentage(count(*), WHERE result != 'SUCCESS') FROM SyntheticCheck"; 
	public static final String queryDuration = "SELECT average(duration) FROM SyntheticCheck TIMESERIES"; 
	
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
		double duration = Insights.parseTimeseriesResponse(responseDuration, "average");
		
		// Open the PDF 
		Document document = new Document(PageSize.LETTER, 50, 50, 50, 50);
		FileOutputStream out = new FileOutputStream(monitorName + ".pdf");
		PdfWriter writer = PdfWriter.getInstance(document, out);
		document.open();
		
		// Title
		Paragraph title = new Paragraph(monitorName + " Report");
		title.setAlignment(Paragraph.ALIGN_CENTER);
		document.add(title);
		document.add(Chunk.NEWLINE);
		
		// Create the results table
		// Reference: http://developers.itextpdf.com/examples/itext-action-second-edition/chapter-4
		PdfPTable table = new PdfPTable(2);
		PdfPCell cell;
		cell = new PdfPCell(new Phrase("Results"));
		cell.setColspan(2);
		cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		table.addCell(cell);
		table.addCell("Success Rate (%)");
		table.addCell(getDecimal(pctSuccess));
		table.addCell("Failure Rate (%)");
		table.addCell(getDecimal(pctFailed));
		table.addCell("Average Response Time (ms)");
		table.addCell(getDecimal(duration));
		document.add(table);
		
		// Create the time series line chart
		// Reference http://developers.itextpdf.com/examples/itext-action-second-edition/chapter-14
		PdfContentByte cb = writer.getDirectContent();
        float width = PageSize.A4.getWidth();
        float height = PageSize.A4.getHeight() / 2;
        PdfTemplate line = cb.createTemplate(width, height);
        Graphics2D g2d2 = new PdfGraphics2D(line, width, height);
        Rectangle2D r2d2 = new Rectangle2D.Double(0, 0, width, height);
        getLineChart(responseDuration).draw(g2d2, r2d2);
        g2d2.dispose();
        cb.addTemplate(line, 0, 0);
		
		// Close the PDF
		document.close();
		System.out.println("Document closed");
	}
	
	private JFreeChart getLineChart(JSONObject responseDuration) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
		// Loop through the responseDuration JSON
		JSONArray tsArr = responseDuration.getJSONArray("timeSeries");
		for (int i=0; i < tsArr.length(); i++) {
			JSONObject tsObject = tsArr.getJSONObject(i);
			double avg = tsObject.getJSONArray("results").getJSONObject(0).getDouble("average");
			dataset.addValue(avg, "default", i + "");
		}
		
		String title = "Response Time";
		String categoryAxisLabel = "";
		String valueAxisLabel = "";
		
		return ChartFactory.createLineChart(title, categoryAxisLabel, valueAxisLabel, dataset,
				PlotOrientation.VERTICAL, false, false, false);
	}
	
	/**
	 * Convert a double to the proper format
	 * 
	 * @param value
	 * @return
	 */
	private String getDecimal(double value) {
		DecimalFormat df = new DecimalFormat("#.##");
		return df.format(value);
	}
	
	private String addToNrql(String nrql, String monitorName) {
		String fullQuery = new String(nrql);
		fullQuery += " WHERE monitorName = '" + monitorName + "'";
		fullQuery += " SINCE 1 week ago";
		return fullQuery;
	}
}
