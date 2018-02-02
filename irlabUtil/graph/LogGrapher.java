package edu.ucla.astro.irlab.util.graph;
/* 
 * This class is used for experimenting with JFreeChart 
 * It is not included in Makefile
 */
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeries;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;


import edu.ucla.astro.irlab.util.TailListener;
import edu.ucla.astro.irlab.util.Tail;

public class LogGrapher implements TailListener {
	String title="Test Chart";
	String timeLabel = "Time";
	String valueLabel = "Temperature";
	TimeSeriesCollection timedata;
	JFreeChart chart;
	SimpleDateFormat dateFormatter;
	public boolean gotFirstSet = false;
	public LogGrapher() {
		timedata = new TimeSeriesCollection();
		dateFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	}

	public void drawChart() {
		//. wait for first set of data
		while (gotFirstSet == false) {
			try {
				Thread.sleep(100);
			} catch (Exception ex) { 
				ex.printStackTrace();
			}
		}
		// create a chart...
		chart = ChartFactory.createTimeSeriesChart(title, timeLabel, valueLabel, timedata, true, false, false);
			
		XYPlot plot = (XYPlot)chart.getPlot();
		plot.setRangeGridlinePaint(Color.BLACK);
		ValueAxis rangeAxis = ((XYPlot)plot).getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		DateAxis domainAxis = (DateAxis)((XYPlot)plot).getDomainAxis();
		domainAxis.setDateFormatOverride(dateFormatter);
		domainAxis.setVerticalTickLabels(true);
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)plot.getRenderer();
		
		renderer.setSeriesStroke(1, new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[] {10.0f, 6.0f}, 0.0f));
		
		// create and display a frame...
		ChartFrame frame = new ChartFrame("First", chart);
		frame.pack();
		frame.setVisible(true);

	}

	public void receiveNewData(byte[] data) {
		String dataStr = new String(data);
		String[] lines = dataStr.split("\n");
		String[] parts;
		String[] dateParts;
		String[] timeParts;
		int numParts;
		for (int ii=0; ii<lines.length; ii++) {
			//System.out.println("<"+lines[ii]+">");
			parts = lines[ii].split(",");
			numParts = parts.length;
			if (lines[ii].endsWith(",")) {
				numParts++;
			}
			dateParts = parts[0].split("/");
			timeParts = parts[1].split(":");
			while (timedata.getSeriesCount() < numParts-3) {
				timedata.addSeries(new TimeSeries("Temp "+timedata.getSeriesCount()+1, Second.class));				
			}
			for (int jj=2; jj<(numParts-1); jj++) {
				timedata.getSeries(jj-2).add(new Second(Integer.valueOf(timeParts[2]), Integer.valueOf(timeParts[1]),
						Integer.valueOf(timeParts[0]), Integer.valueOf(dateParts[2]),
						Integer.valueOf(dateParts[1]), Integer.valueOf("20"+dateParts[0])), 
						Double.valueOf(parts[jj]));
			}
		}
		if (gotFirstSet == false) {
			gotFirstSet = true;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LogGrapher lg = new LogGrapher();
		Tail tail = new Tail("/usr/local/home/mosdev/kroot/kss/mosfire/gui/temp/temp.log");
		tail.addTailListener(lg);
		try {
			tail.startReading();
		} catch (FileNotFoundException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}

		

//		lg.createTestData();
		lg.drawChart();
	}


}
