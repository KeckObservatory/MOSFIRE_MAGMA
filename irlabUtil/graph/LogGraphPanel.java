package edu.ucla.astro.irlab.util.graph;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import edu.ucla.astro.irlab.util.Tail;
import edu.ucla.astro.irlab.util.TailListener;

public class LogGraphPanel extends JPanel implements TailListener {
	JToolBar toolbar;
	JButton openFileButton = new JButton("Open...");
	JButton startTailButton = new JButton("Tail");
	JButton stopTailButton = new JButton("Stop");
	JLabel statusBar = new JLabel("");
	
	Tail tail = new Tail();
	String title="Log Data";
	String timeAxisLabel = "Time";
	String valueAxisLabel = "Value";
	String valueType = "Value";
	TimeSeriesCollection timeData;
	JFreeChart chart;
	ChartPanel chartPanel;
	SimpleDateFormat dateFormatter;
	boolean gotFirstSet = false;
	boolean doToolbar;
	private File currentReadPath;
	private File defaultSaveFile;
	ValueAxis rangeAxis;
	DateAxis domainAxis;
	public LogGraphPanel() {
		this(false);
	}
	public LogGraphPanel(boolean includeToolbar) {
		doToolbar = includeToolbar;
		timeData = new TimeSeriesCollection();
		dateFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		currentReadPath = new File("");
		tail.addTailListener(this);
		init();
	}
	void init() {
		chart = ChartFactory.createTimeSeriesChart(title, timeAxisLabel, valueAxisLabel, null, true, true, false);
		XYPlot plot = (XYPlot)chart.getPlot();
		plot.setRangeGridlinePaint(Color.BLACK);
		rangeAxis = ((XYPlot)plot).getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		domainAxis = (DateAxis)((XYPlot)plot).getDomainAxis();
		domainAxis.setDateFormatOverride(dateFormatter);
		domainAxis.setVerticalTickLabels(true);
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)plot.getRenderer();
		
		renderer.setSeriesStroke(1, new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[] {10.0f, 6.0f}, 0.0f));
		
		chartPanel = new ChartPanel(chart, true);
		chartPanel.setMouseZoomable(true);
		setLayout(new BorderLayout());
		
		if (doToolbar) {
			toolbar = new JToolBar("Log Graph ToolBar");
			openFileButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					openFileButton_actionPerformed();
				}
			});
			startTailButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					startTailButton_actionPerformed();
				}
			});
			stopTailButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					stopTailButton_actionPerformed();
				}
			});
			stopTailButton.setEnabled(false);
			toolbar.add(openFileButton);
			toolbar.add(startTailButton);
			toolbar.add(stopTailButton);
			this.add(toolbar, BorderLayout.NORTH);
		}
		this.add(chartPanel, BorderLayout.CENTER);
		this.add(statusBar, BorderLayout.SOUTH);
	}
	protected void stopTailButton_actionPerformed() {
		stopTail();
	}
	protected void startTailButton_actionPerformed() {
			try {
				startTail();
			} catch (FileNotFoundException ex) {
				JOptionPane.showMessageDialog(this, "Error starting tail.  Log file invalid.", "Error Starting Tail.", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
	}
	protected void openFileButton_actionPerformed() {
		browseForLogFile();
	}

	private void browseForLogFile() {
    //. launch dialog for opening file
    JFileChooser fc = new JFileChooser(currentReadPath);
    fc.setDialogTitle("Open Log File");
    //. filter on *.DDF
//    fc.setFileFilter(new OsirisFileFilters.DDFFileFilter());
    //. initialize with last opened file
    if (defaultSaveFile != null)
      fc.setSelectedFile(defaultSaveFile);
    //. open dialog
    int retVal = fc.showOpenDialog(this);
    //. if ok was hit, open file
    if (retVal == JFileChooser.APPROVE_OPTION) {
      java.io.File file = fc.getSelectedFile();
      //. save path
      currentReadPath = file;
      //. save filename
      defaultSaveFile=file;
      try {
      	//. try to open
      	openLogFile(file.getAbsolutePath());
      	//. set title to log filename
      	setTitle(file.getName());
      	//. indicate file was opened in status bar
      	statusBar.setText("Log file "+file.toString()+" opened.");
      } catch (FileNotFoundException fnfEx) {
      	//. JOptionPane
      	statusBar.setText("Log file "+file.toString()+" not found.");
      }
    }		
	}
	public void setTimeAxisLabel(String label) {
		timeAxisLabel = label;
		domainAxis.setLabel(label);
	}
	public void setValueAxisLabel(String label) {
		valueAxisLabel = label;
		rangeAxis.setLabel(label);
	}
	public void setValueType(String label) {
		valueType = label;
	}
	public void setTitle(String label) {
		title = label;
		chart.setTitle(label);
	}
	public void openLogFile(String logFile) throws FileNotFoundException {
		//		timeData.removeAllSeries();  
		//. TODO the following is faster, but may be a memory leak
		timeData = new TimeSeriesCollection();
		tail.setFilename(logFile);
		gotFirstSet = false;
		tail.startReading();
		
		int timeoutCounter=0;
		//. wait for first set of data
		while (gotFirstSet == false) {
			try {
				Thread.sleep(10);
				timeoutCounter++;
				if (timeoutCounter > 1000) {   //. 5 second timeout
					break;
				}
			} catch (Exception ex) { 
				ex.printStackTrace();
			}
		}
		tail.pauseReading();
		chart.getXYPlot().setDataset(timeData);
		chart.fireChartChanged();
	}
	public void startTail() throws FileNotFoundException {
		tail.resumeReading();
		startTailButton.setEnabled(false);
		stopTailButton.setEnabled(true);
	}
	public void stopTail() {
		tail.pauseReading();
		startTailButton.setEnabled(true);
		stopTailButton.setEnabled(false);
	}
	public void setDefaultLogFilePath(String path) {
		currentReadPath = new File(path);
	}
	public JToolBar getToolBar() {
		return toolbar;
	}
	public void receiveNewData(byte[] data) {
		String dataStr = new String(data);
		String[] lines = dataStr.split("\n");
		String[] parts;
		String[] dateParts;
		String[] timeParts;
		int numParts;
		for (int ii=0; ii<lines.length; ii++) {
			parts = lines[ii].split(",");
			numParts = parts.length;
			if (lines[ii].endsWith(",")) {
				numParts++;
			}
			dateParts = parts[0].split("/");
			timeParts = parts[1].split(":");
			while (timeData.getSeriesCount() < numParts-3) {
				timeData.addSeries(new TimeSeries(valueType+" "+(timeData.getSeriesCount()+1), Second.class));				
			}
			for (int jj=2; jj<(numParts-1); jj++) {
				timeData.getSeries(jj-2).add(new Second(Integer.valueOf(timeParts[2]), Integer.valueOf(timeParts[1]),
						Integer.valueOf(timeParts[0]), Integer.valueOf(dateParts[2]),
						Integer.valueOf(dateParts[1]), Integer.valueOf("20"+dateParts[0])), 
						Double.valueOf(parts[jj]));
			}
		}
		if (gotFirstSet == false) {
			gotFirstSet = true;
		}	
	}
}
