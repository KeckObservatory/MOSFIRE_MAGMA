package edu.ucla.astro.irlab.mosfire.util;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import edu.ucla.astro.irlab.util.PropertyList;
import edu.ucla.astro.irlab.util.graph.LogGraphPanel;

public class MosfireTemperatureLogGraphFrame extends JFrame {
	LogGraphPanel graphPanel;
	PropertyList propList;
	public MosfireTemperatureLogGraphFrame(PropertyList propList) {
		this.propList = propList;
		graphPanel = new LogGraphPanel(true);
		graphPanel.setValueAxisLabel("Temperature (K)");
		graphPanel.setValueType("Temp");
		this.getContentPane().add(graphPanel, BorderLayout.CENTER);

		String logDir = (String)propList.getProperty("TempLogDirectory").getValue();
		String logFile = (String)propList.getProperty("TempLogFile").getValue();
		//. get logdir from properties
		graphPanel.setDefaultLogFilePath(logDir);
			try {
				graphPanel.openLogFile(logDir+File.separator+logFile);
				graphPanel.startTail();
			} catch (FileNotFoundException ex) {
				JOptionPane.showMessageDialog(this, "Error opening temperature log file" +logDir+File.separator+logFile+".", "Error Opening File", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
		
	}
}
